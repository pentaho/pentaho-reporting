/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.openerp;

import com.debortoliwines.openerp.api.Field.FieldType;
import com.debortoliwines.openerp.reporting.di.OpenERPConfiguration;
import com.debortoliwines.openerp.reporting.di.OpenERPFieldInfo;
import com.debortoliwines.openerp.reporting.di.OpenERPFilterInfo;
import com.debortoliwines.openerp.reporting.di.OpenERPHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.util.Base64;
import org.apache.ws.commons.util.Base64.DecodingException;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Pieter van der Merwe
 */
public class OpenERPDataFactory extends AbstractDataFactory {
  private static final long serialVersionUID = -6235833289788633577L;
  private static final Log logger = LogFactory.getLog( OpenERPDataFactory.class );

  private OpenERPConfiguration config;
  private String queryName;

  public OpenERPDataFactory() {
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queryName.equals( query );
  }

  public String[] getQueryNames() {
    return new String[] { queryName };
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public synchronized TableModel queryData( final String query, final DataRow parameters )
    throws ReportDataFactoryException {

    /// TODO: Add more validation Here
    if ( config == null ) {
      throw new ReportDataFactoryException( "Configuration is empty." ); //$NON-NLS-1$
    }

    final TypedTableModel resultSet = new TypedTableModel();

    final int queryLimit = calculateQueryLimit( parameters );
    final OpenERPHelper helper = new OpenERPHelper();
    final OpenERPConfiguration targetConfig = config.clone();
    final ArrayList<OpenERPFilterInfo> configFilters = targetConfig.getFilters();

    // Build a hashmap to pass all parameters as a dictionary to a custom OpenERP procedure
    final HashMap<String, Object> openERPParams = new HashMap<String, Object>();
    for ( final String paramName : parameters.getColumnNames() ) {
      Object value = parameters.get( paramName );
      if ( value == null ) {
        value = false;
      }

      openERPParams.put( paramName, value );
    }

    // Can't get selected fields from config, because we may be calling a custom function
    ArrayList<OpenERPFieldInfo> selectedFields = null;
    try {
      selectedFields = helper.getFields( targetConfig, openERPParams );
    } catch ( Exception e1 ) {
      throw new ReportDataFactoryException( "Failed to select field", e1 );
    }

    // Build a field list
    for ( final OpenERPFieldInfo selectedFld : selectedFields ) {
      resultSet.addColumn( selectedFld.getRenamedFieldName(), convertFieldType( selectedFld.getFieldType() ) );
    }

    // Called by the designer to get column layout, return a empty resultSet with columns already set
    if ( queryLimit == 1 ) {
      return resultSet;
    }

    // Parameter parser to replace parameters in strings
    final PropertyLookupParser parameterParser = new PropertyLookupParser() {
      private static final long serialVersionUID = -7264648195698966110L;

      @Override
      protected String lookupVariable( final String property ) {
        return parameters.get( property ).toString();
      }
    };

    // Replace parameterized filters with values from parameters
    if ( configFilters != null ) {
      for ( final OpenERPFilterInfo filter : configFilters ) {
        // You could have set a filter without using the Designer.  Then the filter could be any data type that
        // should not be converted to a String.
        if ( filter.getValue() instanceof String ) {
          try {
            final String realFilterValue = filter.getValue().toString();

            // If you specify the filter on its own, try in get the object value
            // Not all parameter values are a string.  Could be an Object[] of ids for example in a multi-select
            // parameter
            final Object filterValue;
            if ( realFilterValue.length() >= 4
              && realFilterValue.substring( 0, 2 ).equals( "${" )
              && realFilterValue.endsWith( "}" ) ) {

              final String parameterName = realFilterValue.substring( 2, realFilterValue.length() - 1 );
              filterValue = parameters.get( parameterName );
            }
            // Cater for cases where users specify compound filer: "name" "like" "some${post_fix}"
            else {
              filterValue = parameterParser.translateAndLookup( realFilterValue, parameters );
            }

            // If the value is null, this may be a dependent query and it is waiting for a parameter.
            // just return and wait
            if ( filterValue == null ) {
              return resultSet;
            }

            filter.setValue( filterValue );
          } catch ( Exception e ) {
            throw new ReportDataFactoryException( e.getMessage(), e );
          }
        }
      }
    }

    // Get the data
    final Object[][] rows;
    try {
      rows = helper.getData( targetConfig, openERPParams );
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( e.getMessage(), e );
    }

    // Add data to resultSet and do some data transformations
    for ( int row = 0; row < rows.length; row++ ) {
      final Object[] rowData = rows[ row ];

      for ( int column = 0; column < selectedFields.size(); column++ ) {
        final OpenERPFieldInfo fld = selectedFields.get( column );

        // Base64 Decode Binary
        if ( fld.getFieldType() == FieldType.BINARY
          && rowData[ column ] != null ) {
          try {
            rowData[ column ] = Base64.decode( rowData[ column ].toString() );
          } catch ( DecodingException e ) {
            rowData[ column ] = "Unable to decode string";
          } catch ( Exception e ) {
            logger.debug( "Failed to decode string on query-result: Row=" + row + " Col=" + column, e );
          }
        }

        // Only return integer part (exclude name) from many2one field
        if ( fld.getFieldType() == FieldType.MANY2ONE
          && rowData[ column ] instanceof Object[] ) {
          final Object[] value = (Object[]) rowData[ column ];
          rowData[ column ] = Integer.parseInt( String.valueOf( value[ 0 ] ) );
        }

        // make many2many and one2many a comma separated list of values
        if ( ( fld.getFieldType() == FieldType.MANY2MANY || fld.getFieldType() == FieldType.ONE2MANY )
          && rowData[ column ] instanceof Object[] ) {

          final StringBuilder stringValue = new StringBuilder();
          final Object[] mcolumn = (Object[]) rowData[ column ];
          for ( int x = 0; x < mcolumn.length; x += 1 ) {
            if ( x != 0 ) {
              stringValue.append( ',' );
            }
            stringValue.append( mcolumn[ x ] );
          }

          rowData[ column ] = stringValue.toString();
        }
      }

      resultSet.addRow( rowData );
    }
    return resultSet;
  }

  private Class<?> convertFieldType( final FieldType fieldType ) {
    switch( fieldType ) {
      case BINARY:
        return Byte[].class;
      case BOOLEAN:
        return Boolean.class;
      case INTEGER:
        return Integer.class;
      case FLOAT:
        return Float.class;
      case DATETIME:
      case DATE:
        return Date.class;
      case MANY2ONE:
        return Integer.class;
      case ONE2MANY:
      case MANY2MANY:
      case CHAR:
      case TEXT:
      default:
        return String.class;
    }
  }

  public void setConfig( final OpenERPConfiguration config ) {
    this.config = config;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName( final String queryName ) {
    this.queryName = queryName;
  }

  public void close() {
  }

  public OpenERPDataFactory clone() {
    final OpenERPDataFactory dataFactory = (OpenERPDataFactory) super.clone();
    if ( this.config != null ) {
      dataFactory.config = this.config.clone();
    }
    return dataFactory;
  }

  public OpenERPConfiguration getConfig() {
    return config;
  }
}
