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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.DefaultTableMetaData;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.ImmutableTableMetaData;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableMetaData;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TypeMapper;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeCache;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.ImmutableDataAttributes;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

/**
 * Creates a <code>TableModel</code> which is backed up by a <code>ResultSet</code>. If the <code>ResultSet</code> is
 * scrollable, a {@link ScrollableResultSetTableModel} is created, otherwise all data is copied from the
 * <code>ResultSet</code> into a <code>DefaultTableModel</code>.
 * <p/>
 * The creation of a <code>DefaultTableModel</code> can be forced if the system property
 * <code>"org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableFactoryMode"</code> is set to
 * <code>"simple"</code>.
 *
 * @author Thomas Morgner
 */
public final class ResultSetTableModelFactory {
  private static final Log logger = LogFactory.getLog( ResultSetTableModelFactory.class );
  /**
   * The configuration key defining how to map column names to column indices.
   */
  public static final String COLUMN_NAME_MAPPING_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ColumnMappingMode"; //$NON-NLS-1$

  /**
   * The 'ResultSet factory mode'.
   */
  public static final String RESULTSET_FACTORY_MODE
      = "org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableFactoryMode"; //$NON-NLS-1$

  /**
   * Singleton instance of the factory.
   */
  private static ResultSetTableModelFactory defaultInstance;

  /**
   * Default constructor. This is a Singleton, use getInstance().
   */
  private ResultSetTableModelFactory() {
  }

  /**
   * Creates a table model by using the given <code>ResultSet</code> as the backend. If the <code>ResultSet</code> is
   * scrollable (the type is not <code>TYPE_FORWARD_ONLY</code>), an instance of {@link
   * org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ScrollableResultSetTableModel} is returned.
   * This model uses the extended capabilities of scrollable result sets to directly read data from the database without
   * caching or the need of copying the complete <code>ResultSet</code> into the programs memory.
   * <p/>
   * If the <code>ResultSet</code> lacks the scrollable features, the data will be copied into a
   * <code>DefaultTableModel</code> and the <code>ResultSet</code> gets closed.
   *
   * @param rs                the result set.
   * @param columnNameMapping defines, whether to use column names or column labels to compute the column index. If
   *                          true, then we map the Name.  If false, then we map the Label
   * @param closeStatement    a flag indicating whether closing the resultset should also close the statement.
   * @return a closeable table model.
   * @throws SQLException if there is a problem with the result set.
   */
  public CloseableTableModel createTableModel( final ResultSet rs,
                                               final boolean columnNameMapping,
                                               final boolean closeStatement )
      throws SQLException {
    // Allow for override, some jdbc drivers are buggy :(
    final String prop =
        ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            ResultSetTableModelFactory.RESULTSET_FACTORY_MODE, "auto" ); //$NON-NLS-1$

    if ( "simple".equalsIgnoreCase( prop ) ) { //$NON-NLS-1$
      return generateDefaultTableModel( rs, columnNameMapping );
    }

    int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
    try {
      resultSetType = rs.getType();
    } catch ( SQLException sqle ) {
      ResultSetTableModelFactory.logger.info(
          "ResultSet type could not be determined, assuming default table model." ); //$NON-NLS-1$
    }
    if ( resultSetType == ResultSet.TYPE_FORWARD_ONLY ) {
      return generateDefaultTableModel( rs, columnNameMapping );
    } else {
      rs.last();
      int rowCount = rs.getRow();
      rs.beforeFirst();
      if ( rowCount < 500 ) {
        return generateDefaultTableModel( rs, columnNameMapping );
      }
      return new ScrollableResultSetTableModel( rs, columnNameMapping, closeStatement );
    }
  }

  /**
   * A DefaultTableModel that implements the CloseableTableModel interface.
   */
  private static final class CloseableDefaultTableModel extends DefaultTableModel
      implements CloseableTableModel, MetaTableModel {
    private TableMetaData metaData;
    private Class[] columnTypes;

    private static final Object[] EMPTY_ARRAY = new Object[ 0 ];
    private static final Object[][] EMPTY_DATA_VECTOR = new Object[ 0 ][ 0 ];


    /**
     * Creates a new closeable table model.
     *
     * @param rowData     the table data.
     * @param columnNames the column names.
     */
    private CloseableDefaultTableModel( final Object[][] rowData,
                                        final Object[] columnNames,
                                        final Class[] columnTypes,
                                        final TableMetaData metaTableModel ) {
      super( rowData, columnNames );
      this.columnTypes = columnTypes;
      this.metaData = metaTableModel;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    public Class getColumnClass( final int columnIndex ) {
      if ( columnTypes == null ) {
        return Object.class;
      }
      if ( columnIndex >= columnTypes.length ) {
        return Object.class;
      }
      return columnTypes[ columnIndex ];
    }

    /**
     * If this model has a resultset assigned, close it, if this is a DefaultTableModel, remove all data.
     */
    public void close() {
      setDataVector( CloseableDefaultTableModel.EMPTY_DATA_VECTOR, CloseableDefaultTableModel.EMPTY_ARRAY );
    }

    /**
     * Returns the meta-attribute as Java-Object. The object type that is expected by the report engine is defined in
     * the TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model
     * into a model suitable for reporting.
     * <p/>
     * Meta-data models that only describe meta-data for columns can ignore the row-parameter.
     *
     * @param row    the row of the cell for which the meta-data is queried.
     * @param column the index of the column for which the meta-data is queried.
     * @return the meta-data object.
     */
    public DataAttributes getCellDataAttributes( final int row, final int column ) {
      if ( metaData == null ) {
        return EmptyDataAttributes.INSTANCE;
      }
      return metaData.getCellDataAttribute( row, column );
    }

    public boolean isCellDataAttributesSupported() {
      return metaData.isCellDataAttributesSupported();
    }

    public DataAttributes getColumnAttributes( final int column ) {
      if ( metaData == null ) {
        return EmptyDataAttributes.INSTANCE;
      }
      return metaData.getColumnAttribute( column );
    }

    /**
     * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
     * hints on the sort-order of the data.
     *
     * @return
     */
    public DataAttributes getTableAttributes() {
      if ( metaData == null ) {
        return null;
      }
      return metaData.getTableAttribute();
    }
  }

  /**
   * Generates a <code>TableModel</code> that gets its contents filled from a <code>ResultSet</code>. The column names
   * of the <code>ResultSet</code> will form the column names of the table model.
   * <p/>
   * Hint: To customize the names of the columns, use the SQL column aliasing (done with <code>SELECT nativecolumnname
   * AS "JavaColumnName" FROM ....</code>
   *
   * @param rs                the result set.
   * @param columnNameMapping defines, whether to use column names or column labels to compute the column index. If
   *                          true, then we map the Name.  If false, then we map the Label
   * @return a closeable table model.
   * @throws SQLException if there is a problem with the result set.
   */
  public CloseableTableModel generateDefaultTableModel( final ResultSet rs, final boolean columnNameMapping )
      throws SQLException {
    try {
      final ResultSetMetaData rsmd = rs.getMetaData();
      final int colcount = rsmd.getColumnCount();
      final Class[] colTypes = TypeMapper.mapTypes( rsmd );
      //final DefaultTableMetaData metaData = new DefaultTableMetaData( colcount );

      // In past many database drivers were returning same value for column label and column name. So it is
      // inconsistent
      // what the database driver will return for column name vs column label.
      // We have a legacy configuration for this. If set, then if column label is null or empty then return column
      // name.
      // Otherwise return column label.
      // If non-legacy mode, then we return exactly what the JDBC driver returns (label for label, name for name)
      // without
      // any interpretation or interpolation.
      final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
      final boolean useLegacyColumnMapping =
          "legacy".equalsIgnoreCase(                                                                            // NON-NLS
              globalConfig.getConfigProperty(
                  "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ColumnMappingMode",
                  "legacy" ) );  // NON-NLS

      final String[] header = new String[ colcount ];
      final AttributeMap[] columnMeta = new AttributeMap[ colcount ];

      for ( int columnIndex = 0; columnIndex < colcount; columnIndex++ ) {
        String columnLabel = rsmd.getColumnLabel( columnIndex + 1 );
        if ( useLegacyColumnMapping ) {
          if ( ( columnLabel == null ) || ( columnLabel.isEmpty() ) ) {
            // We are in legacy mode and column label is either null or empty, we then use column name instead.
            columnLabel = rsmd.getColumnName( columnIndex + 1 );
          }
          header[ columnIndex ] = columnLabel;
        } else {
          if ( columnNameMapping ) {
            header[ columnIndex ] = rsmd.getColumnName( columnIndex + 1 );
          } else {
            header[ columnIndex ] = columnLabel;
          }
        }

        columnMeta[ columnIndex ] = ResultSetTableModelFactory.collectData( rsmd, columnIndex, header[ columnIndex ] );
      }

      final Object[][] rowMap = produceData( rs, colcount );
      ImmutableTableMetaData metaData = new ImmutableTableMetaData( ImmutableDataAttributes.EMPTY,
          map( columnMeta ) );
      return new CloseableDefaultTableModel( rowMap, header, colTypes, metaData );
    } finally {
      Statement statement = null;
      try {
        statement = rs.getStatement();
      } catch ( SQLException sqle ) {
        // yeah, whatever
        logger.warn( "Failed to close statement", sqle );
      }
      try {
        rs.close();
      } catch ( SQLException sqle ) {
        // yeah, whatever
        logger.warn( "Failed to close resultset", sqle );
      }
      try {
        if ( statement != null ) {
          statement.close();
        }
      } catch ( SQLException sqle ) {
        // yeah, whatever
        logger.warn( "Failed to close statement", sqle );
      }
    }
  }

  public static ImmutableDataAttributes[] map( AttributeMap[] data ) {
    DataAttributeCache cache = ClassicEngineBoot.getInstance().getObjectFactory().get( DataAttributeCache.class );
    DataAttributeContext ctx = new DefaultDataAttributeContext();
    ImmutableDataAttributes[] retval = new ImmutableDataAttributes[ data.length ];
    for ( int i = 0; i < data.length; i++ ) {
      AttributeMap<Object> map = data[ i ];
      if ( cache != null ) {
        retval[ i ] = cache.normalize( new ImmutableDataAttributes( map ), ctx );
      } else {
        retval[ i ] = new ImmutableDataAttributes( map );
      }
    }
    return retval;
  }

  protected Object[][] produceData( final ResultSet rs, final int colcount ) throws SQLException {
    final ArrayList<Object[]> rows = new ArrayList<Object[]>();
    while ( rs.next() ) {
      final Object[] column = new Object[ colcount ];
      for ( int i = 0; i < colcount; i++ ) {
        final Object val = rs.getObject( i + 1 );
        try {
          if ( val instanceof Blob ) {
            column[ i ] = IOUtils.getInstance().readBlob( (Blob) val );
          } else if ( val instanceof Clob ) {
            column[ i ] = IOUtils.getInstance().readClob( (Clob) val );
          } else {
            column[ i ] = val;
          }
        } catch ( IOException ioe ) {
          logger.error( "IO error while copying data.", ioe );
          throw new SQLException( "IO error while copying data: " + ioe.getMessage() );
        }
      }
      rows.add( column );
    }

    return rows.toArray( new Object[ rows.size() ][] );
  }

  public static AttributeMap<Object> collectData( final ResultSetMetaData rsmd,
                                                    final int column,
                                                    final String name )
      throws SQLException {
    AttributeMap<Object> metaData = new AttributeMap<Object>();
    metaData.setAttribute( MetaAttributeNames.Core.NAMESPACE,
        MetaAttributeNames.Core.TYPE, TypeMapper.mapForColumn( rsmd, column ) );
    metaData.setAttribute( MetaAttributeNames.Core.NAMESPACE,
        MetaAttributeNames.Core.NAME, name );
    try {
      if ( rsmd.isCurrency( column + 1 ) ) {
        metaData.setAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.CURRENCY, Boolean.TRUE );
      } else {
        metaData.setAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.CURRENCY, Boolean.FALSE );
      }
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#isCurrency. Driver does not implement the JDBC specs correctly. ", e );
    }
    try {

      if ( rsmd.isSigned( column + 1 ) ) {
        metaData.setAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SIGNED, Boolean.TRUE );
      } else {
        metaData.setAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SIGNED, Boolean.FALSE );
      }
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#isSigned. Driver does not implement the JDBC specs correctly. ", e );
    }

    try {
      final String tableName = rsmd.getTableName( column + 1 );
      if ( tableName != null ) {
        metaData.setAttribute( MetaAttributeNames.Database.NAMESPACE, MetaAttributeNames.Database.TABLE, tableName );
      }
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getTableName. Driver does not implement the JDBC specs correctly. ", e );
    }

    try {
      final String schemaName = rsmd.getSchemaName( column + 1 );
      if ( schemaName != null ) {
        metaData.setAttribute( MetaAttributeNames.Database.NAMESPACE, MetaAttributeNames.Database.SCHEMA, schemaName );
      }
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getSchemaName. Driver does not implement the JDBC specs correctly. ", e );
    }


    try {
      final String catalogName = rsmd.getCatalogName( column + 1 );
      if ( catalogName != null ) {
        metaData.setAttribute( MetaAttributeNames.Database.NAMESPACE, MetaAttributeNames.Database.CATALOG, catalogName );
      }
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getTableName. Driver does not implement the JDBC specs correctly. ", e );
    }

    try {
      final String label = rsmd.getColumnLabel( column + 1 );
      if ( label != null ) {
        metaData.setAttribute( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL, label );
      }
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getTableName. Driver does not implement the JDBC specs correctly. ", e );
    }

    try {
      final int displaySize = rsmd.getColumnDisplaySize( column + 1 );
      metaData.setAttribute( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.DISPLAY_SIZE,
          IntegerCache.getInteger( displaySize ) );
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getTableName. Driver does not implement the JDBC specs correctly. ", e );
    }

    try {
      final int precision = rsmd.getPrecision( column + 1 );
      metaData.setAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.PRECISION,
          IntegerCache.getInteger( precision ) );
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getTableName. Driver does not implement the JDBC specs correctly. ", e );
    }

    try {
      final int scale = rsmd.getScale( column + 1 );
      metaData.setAttribute( MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SCALE,
          IntegerCache.getInteger( scale ) );
    } catch ( SQLException e ) {
      logger.debug( "Error on ResultSetMetaData#getTableName. Driver does not implement the JDBC specs correctly. ", e );
    }
    return metaData;
  }

  /**
   * No longer used.
   *
   * @param rsmd
   * @param metaData
   * @param column
   */
  @Deprecated
  public static void updateMetaData( final ResultSetMetaData rsmd,
                                     final DefaultTableMetaData metaData,
                                     final int column ) {
    try {
      if ( rsmd.isCurrency( column + 1 ) ) {
        metaData.setColumnAttribute( column, MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.CURRENCY,
            Boolean.TRUE );
      } else {
        metaData.setColumnAttribute( column, MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.CURRENCY,
            Boolean.FALSE );
      }

      if ( rsmd.isSigned( column + 1 ) ) {
        metaData.setColumnAttribute( column, MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SIGNED,
            Boolean.TRUE );
      } else {
        metaData.setColumnAttribute( column, MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SIGNED,
            Boolean.FALSE );
      }

      final String tableName = rsmd.getTableName( column + 1 );
      if ( tableName != null ) {
        metaData.setColumnAttribute( column, MetaAttributeNames.Database.NAMESPACE, MetaAttributeNames.Database.TABLE,
            tableName );
      }
      final String schemaName = rsmd.getSchemaName( column + 1 );
      if ( schemaName != null ) {
        metaData.setColumnAttribute( column, MetaAttributeNames.Database.NAMESPACE, MetaAttributeNames.Database.SCHEMA,
            schemaName );
      }
      final String catalogName = rsmd.getCatalogName( column + 1 );
      if ( catalogName != null ) {
        metaData.setColumnAttribute( column, MetaAttributeNames.Database.NAMESPACE,
            MetaAttributeNames.Database.CATALOG, catalogName );
      }
      final String label = rsmd.getColumnLabel( column + 1 );
      if ( label != null ) {
        metaData.setColumnAttribute( column, MetaAttributeNames.Formatting.NAMESPACE,
            MetaAttributeNames.Formatting.LABEL, label );
      }
      final int displaySize = rsmd.getColumnDisplaySize( column + 1 );
      metaData.setColumnAttribute( column, MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.DISPLAY_SIZE, IntegerCache.getInteger( displaySize ) );

      final int precision = rsmd.getPrecision( column + 1 );
      metaData.setColumnAttribute( column, MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.PRECISION,
          IntegerCache.getInteger( precision ) );
      final int scale = rsmd.getScale( column + 1 );
      metaData.setColumnAttribute( column, MetaAttributeNames.Numeric.NAMESPACE, MetaAttributeNames.Numeric.SCALE,
          IntegerCache.getInteger( scale ) );
    } catch ( SQLException sqle ) {
      // It is non-fatal if the meta-data cannot be read from the result set. Drivers are
      // buggy all the time ..
    }
  }

  /**
   * Returns the singleton instance of the factory.
   *
   * @return an instance of this factory.
   */
  public static synchronized ResultSetTableModelFactory getInstance() {
    if ( defaultInstance == null ) {
      defaultInstance = new ResultSetTableModelFactory();
    }
    return defaultInstance;
  }

}
