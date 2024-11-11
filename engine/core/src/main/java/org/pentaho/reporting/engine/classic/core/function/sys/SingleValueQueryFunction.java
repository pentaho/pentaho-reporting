/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.function.sys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;

import javax.swing.table.TableModel;

/**
 * Fires a query against the data-source and returns a single value. The current data-row is used as source for the
 * parameters of the query. The parameters that should be used must be declared as list of fields.
 *
 * @author Thomas Morgner
 * @deprecated use the formula expression with the same name instead.
 */
public class SingleValueQueryFunction extends ColumnAggregationExpression {
  private static class QueryParametersDataRow extends StaticDataRow {
    private QueryParametersDataRow( final DataRow globalView, final ParameterMapping[] parameterMappings ) {
      final String[] outerNames = new String[parameterMappings.length];
      final Object[] values = new Object[parameterMappings.length];
      for ( int i = 0; i < parameterMappings.length; i++ ) {
        final ParameterMapping mapping = parameterMappings[i];
        final String name = mapping.getAlias();
        values[i] = globalView.get( name );
        outerNames[i] = mapping.getName();

      }
      setData( outerNames, values );
    }
  }

  private static final Log logger = LogFactory.getLog( SingleValueQueryFunction.class );

  /**
   * The name of the query that should be executed.
   */
  private String query;
  /**
   * The name of the column of the query's result-table that should be used. If undefined, the first column is used.
   */
  private String resultColumn;

  private int queryTimeout;

  /**
   * Default Constructor.
   */
  public SingleValueQueryFunction() {
  }

  public int getQueryTimeout() {
    return queryTimeout;
  }

  public void setQueryTimeout( final int queryTimeout ) {
    this.queryTimeout = queryTimeout;
  }

  /**
   * Returns the name of the result-column. The result-column specified the name of the column of the query's
   * result-table that should be used. If undefined, the first column is used.
   *
   * @return the result column name.
   */
  public String getResultColumn() {
    return resultColumn;
  }

  /**
   * Defines the name of the result-column. The result-column specified the name of the column of the query's
   * result-table that should be used. If undefined, the first column is used.
   *
   * @param resultColumn
   *          the result column name.
   */
  public void setResultColumn( final String resultColumn ) {
    this.resultColumn = resultColumn;
  }

  /**
   * Returns the query name.
   *
   * @return the query name.
   */
  public String getQuery() {
    return query;
  }

  /**
   * Defines the query name.
   *
   * @param query
   *          the query name.
   */
  public void setQuery( final String query ) {
    this.query = query;
  }

  /**
   * Performs the query by collecting the data from the datarow and executing the query.
   *
   * @return the computed value.
   */
  private Object performQuery() {
    if ( query == null ) {
      return null;
    }
    try {
      final DataFactory dataFactory = getRuntime().getDataFactory();
      final String[] fields = getField();
      final int length = fields.length;
      final ParameterMapping[] mappings = new ParameterMapping[length];
      for ( int i = 0; i < length; i++ ) {
        mappings[i] = new ParameterMapping( fields[i], fields[i] );
      }

      final QueryParametersDataRow params = new QueryParametersDataRow( getDataRow(), mappings );
      final TableModel tableModel = dataFactory.queryData( query, new QueryDataRowWrapper( params, 1, queryTimeout ) );
      if ( tableModel == null ) {
        return null;
      }
      final int columnCount = tableModel.getColumnCount();
      if ( tableModel.getRowCount() == 0 || columnCount == 0 ) {
        return null;
      }
      if ( resultColumn == null ) {
        return tableModel.getValueAt( 0, 0 );
      }
      for ( int i = 0; i < columnCount; i++ ) {
        if ( resultColumn.equals( tableModel.getColumnName( i ) ) ) {
          return tableModel.getValueAt( 0, i );
        }
      }
      // do nothing ..
    } catch ( Exception e ) {
      logger.warn( "SingleValueQueryFunction: Failed to perform query", e );
    }
    return null;
  }

  /**
   * Returns the query result.
   *
   * @return the query result.
   */
  public Object getValue() {
    return performQuery();
  }
}
