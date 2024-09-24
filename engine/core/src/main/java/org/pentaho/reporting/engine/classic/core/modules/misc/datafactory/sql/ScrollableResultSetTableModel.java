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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.AbstractTableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.DataTableException;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.ImmutableTableMetaData;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableMetaData;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TypeMapper;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.ImmutableDataAttributes;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

/**
 * A tableModel which is backed up by a java.sql.ResultSet. Use this to directly feed your database data into
 * JFreeReport. If you have trouble using this TableModel and you have either enough memory or your query result is not
 * huge, you may want to use <code>ResultSetTableModelFactory.generateDefaultTableModel (ResultSet rs)</code>. That
 * implementation will read all data from the given ResultSet and keep that data in memory.
 * <p/>
 * Use the close() function to close the ResultSet contained in this model.
 *
 * @author Thomas Morgner
 */
public class ScrollableResultSetTableModel extends AbstractTableModel
  implements CloseableTableModel, MetaTableModel {
  public static final String COL_MAPPING_KEY = "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ColumnMappingMode";
  /**
   * The scrollable ResultSet source.
   */
  private ResultSet resultset;
  /**
   * The ResultSetMetaData object for this result set.
   */
  private ResultSetMetaData dbmd;
  /**
   * The number of rows in the result set.
   */
  private int rowCount;
  /**
   * Defines the column naming mode.
   */
  private final boolean columnNameMapping;
  private boolean closeStatement;
  /**
   * The column types as read from the result set.
   */
  private Class[] types;

  private TableMetaData metaData;

  /**
   * Constructs the model.
   *
   * @param resultset         the result set.
   * @param columnNameMapping defines, whether to use column names or column labels to compute the column index.
   * @param closeStatement    a flag indicating whether the statement, that created the resultset should be closed when
   *                          the resultset gets closed.
   * @throws SQLException if there is a problem with the result set.
   */
  public ScrollableResultSetTableModel( final ResultSet resultset,
                                        final boolean columnNameMapping,
                                        final boolean closeStatement )
    throws SQLException {
    this.columnNameMapping = columnNameMapping;
    this.closeStatement = closeStatement;
    this.rowCount = -1;
    if ( resultset != null ) {
      updateResultSet( resultset );
    } else {
      close();
    }
  }

  /**
   * Returns the column name mode used to map column names into column indices. If true, then the Name is used, else the
   * Label is used.
   *
   * @return true, if the column name is used for the mapping, false otherwise.
   * @see ResultSetMetaData#getColumnLabel
   * @see ResultSetMetaData#getColumnName
   */
  public boolean isColumnNameMapping() {
    return columnNameMapping;
  }

  /**
   * Updates the result set in this model with the given ResultSet object.
   *
   * @param resultset the new result set.
   * @throws SQLException if there is a problem with the result set.
   */
  public void updateResultSet( final ResultSet resultset )
    throws SQLException {
    if ( this.resultset != null ) {
      close();
    }

    this.resultset = resultset;
    this.dbmd = resultset.getMetaData();
    final int colcount = dbmd.getColumnCount();
    AttributeMap<Object>[] columnMeta = new AttributeMap[colcount];
    for ( int i = 0; i < colcount; i++ ) {
      columnMeta[i] = ResultSetTableModelFactory.collectData( dbmd, i, getColumnName( i ) );
    }

    this.metaData = new ImmutableTableMetaData( ImmutableDataAttributes.EMPTY,
                                                ResultSetTableModelFactory.map( columnMeta ) );

    if ( resultset.last() ) {
      rowCount = resultset.getRow();
    } else {
      rowCount = 0;
    }

    fireTableStructureChanged();
  }

  /**
   * Clears the model of the current result set. The resultset is closed.
   */
  public void close() {
    // Close the old result set if needed.
    if ( resultset != null ) {
      Statement statement = null;
      try {
        statement = resultset.getStatement();
      } catch ( SQLException sqle ) {
        // yeah, whatever
        // logger.warn("Failed to close statement", sqle);
      }
      try {
        resultset.close();
      } catch ( SQLException e ) {
        // Just in case the JDBC driver can't close a result set twice.
        // e.printStackTrace();
        // Closing is fine if it fails ..
      }

      if ( closeStatement ) {
        try {
          if ( statement != null ) {
            statement.close();
          }
        } catch ( SQLException sqle ) {
          // yeah, whatever
        }
      }
    }
    resultset = null;
    dbmd = null;
    rowCount = 0;
    fireTableStructureChanged();
  }

  /**
   * Get a rowCount. This can be a very expensive operation on large datasets. Returns -1 if the total amount of rows is
   * not known to the result set.
   *
   * @return the row count.
   */
  public int getRowCount() {
    if ( resultset == null ) {
      return 0;
    }

    if ( rowCount > -1 ) {
      return rowCount;
    }

    try {
      if ( resultset.last() ) {
        rowCount = resultset.getRow();
        if ( rowCount == -1 ) {
          rowCount = 0;
        }
      } else {
        rowCount = 0;
      }
    } catch ( SQLException sqle ) {
      //Log.debug ("GetRowCount failed, returning 0 rows", sqle);
      throw new DataTableException( "Accessing the result set failed: ", sqle );
    }
    return rowCount;
  }

  /**
   * Returns the number of columns in the ResultSet. Returns 0 if no result set is set or the column count could not be
   * retrieved.
   *
   * @return the column count.
   * @see java.sql.ResultSetMetaData#getColumnCount()
   */
  public int getColumnCount() {
    if ( resultset == null ) {
      return 0;
    }

    if ( dbmd != null ) {
      try {
        return dbmd.getColumnCount();
      } catch ( SQLException e ) {
        //Log.debug ("GetColumnCount failed", e);
        throw new DataTableException( "Accessing the result set failed: ", e );
      }
    }
    return 0;
  }

  /**
   * Returns the columnLabel or column name for the given column. Whether the label or the name is returned depends on
   * the label map mode.
   *
   * @param column the column index.
   * @return the column name.
   * @see java.sql.ResultSetMetaData#getColumnLabel(int)
   */
  public String getColumnName( final int column ) {
    if ( dbmd != null ) {
      try {
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
          "legacy".equalsIgnoreCase( globalConfig.getConfigProperty( COL_MAPPING_KEY, "legacy" ) );  // NON-NLS

        String columnLabel = dbmd.getColumnLabel( column + 1 );
        if ( useLegacyColumnMapping ) {
          if ( ( columnLabel == null ) || ( columnLabel.isEmpty() ) ) {
            // We are in legacy mode and column label is either null or empty, we then use column name instead.
            columnLabel = dbmd.getColumnName( column + 1 );
          }

          return columnLabel;
        } else {
          if ( isColumnNameMapping() ) {
            return dbmd.getColumnName( column + 1 );
          } else {
            return columnLabel;
          }
        }
      } catch ( SQLException e ) {
        throw new DataTableException( "Accessing the result set failed: ", e );
      }
    }
    return null;
  }

  /**
   * Returns the value of the specified row and the specified column from within the resultset.
   *
   * @param row    the row index.
   * @param column the column index.
   * @return the value.
   */
  public Object getValueAt( final int row, final int column ) {
    if ( resultset != null ) {
      try {
        resultset.absolute( row + 1 );
        return resultset.getObject( column + 1 );
      } catch ( SQLException e ) {
        throw new DataTableException( "Accessing the result set failed: ", e );
      }
    }
    return null;
  }

  /**
   * Returns the class of the resultset column. Returns Object.class if an error occurred.
   *
   * @param column the column index.
   * @return the column class.
   */
  public Class getColumnClass( final int column ) {
    if ( types != null ) {
      return types[ column ];
    }
    if ( dbmd != null ) {
      try {
        types = TypeMapper.mapTypes( dbmd );
        if ( types != null ) {
          return types[ column ];
        }
      } catch ( Exception e ) {
        throw new DataTableException( "Accessing the result set failed: ", e );
      }
    }
    return Object.class;
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the report engine is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
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
