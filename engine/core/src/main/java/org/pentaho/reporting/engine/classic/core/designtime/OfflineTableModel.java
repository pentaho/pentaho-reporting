/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class OfflineTableModel implements MetaTableModel {
  private Object[] values;
  private Class[] columnTypes;
  private String[] columnNames;
  private int columnCount;
  private DefaultDataAttributes tableAttributes;
  private DefaultDataAttributes[] columnAttributes;

  public OfflineTableModel( final TableModel model, final DataAttributeContext dataAttributeContext ) {
    columnCount = model.getColumnCount();
    columnTypes = new Class[columnCount];
    columnNames = new String[columnCount];
    columnAttributes = new DefaultDataAttributes[columnCount];
    values = new Object[columnCount];
    tableAttributes = new DefaultDataAttributes();

    for ( int i = 0; i < columnCount; i++ ) {
      columnTypes[i] = model.getColumnClass( i );
      columnNames[i] = model.getColumnName( i );
      columnAttributes[i] = new DefaultDataAttributes();
    }
    if ( model instanceof MetaTableModel ) {
      final MetaTableModel metaTableModel = (MetaTableModel) model;
      tableAttributes.merge( metaTableModel.getTableAttributes(), dataAttributeContext );

      for ( int i = 0; i < columnCount; i++ ) {
        columnAttributes[i].merge( metaTableModel.getColumnAttributes( i ), dataAttributeContext );
      }
    }
    if ( model.getRowCount() > 0 ) {
      for ( int i = 0; i < columnCount; i++ ) {
        values[i] = model.getValueAt( 0, i );
      }
    }
  }

  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the caller is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Be aware that cell-level attributes do not make it into the designtime dataschema, as this dataschema only looks at
   * the structural metadata available and does not contain any data references.
   *
   * @param row
   *          the row of the cell for which the meta-data is queried.
   * @param column
   *          the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  /**
   * Checks, whether cell-data attributes are supported by this tablemodel implementation.
   *
   * @return true, if the model supports cell-level attributes, false otherwise.
   */
  public boolean isCellDataAttributesSupported() {
    return false;
  }

  /**
   * Returns the column-level attributes for the given column.
   *
   * @param column
   *          the column.
   * @return data-attributes, never null.
   */
  public DataAttributes getColumnAttributes( final int column ) {
    return columnAttributes[column];
  }

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return the table-attributes, never null.
   */
  public DataAttributes getTableAttributes() {
    return tableAttributes;
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display. This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return 1;
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Returns the name of the column at <code>columnIndex</code>. This is used to initialize the table's column header
   * name. Note: this name does not need to be unique; two columns in a table can have the same name.
   *
   * @param columnIndex
   *          the index of the column
   * @return the name of the column
   */
  public String getColumnName( final int columnIndex ) {
    return columnNames[columnIndex];
  }

  /**
   * Returns the most specific superclass for all the cell values in the column. This is used by the <code>JTable</code>
   * to set up a default renderer and editor for the column.
   *
   * @param columnIndex
   *          the index of the column
   * @return the common ancestor class of the object values in the model.
   */
  public Class getColumnClass( final int columnIndex ) {
    return columnTypes[columnIndex];
  }

  /**
   * Returns true if the cell at <code>rowIndex</code> and <code>columnIndex</code> is editable. Otherwise,
   * <code>setValueAt</code> on the cell will not change the value of that cell.
   *
   * @param rowIndex
   *          the row whose value to be queried
   * @param columnIndex
   *          the column whose value to be queried
   * @return true if the cell is editable
   * @see #setValueAt
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return false;
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex
   *          the row whose value is to be queried
   * @param columnIndex
   *          the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    if ( rowIndex == 0 ) {
      return values[columnIndex];
    }
    return null;
  }

  /**
   * Sets the value in the cell at <code>columnIndex</code> and <code>rowIndex</code> to <code>aValue</code>.
   *
   * @param aValue
   *          the new value
   * @param rowIndex
   *          the row whose value is to be changed
   * @param columnIndex
   *          the column whose value is to be changed
   * @see #getValueAt
   * @see #isCellEditable
   */
  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    // ignored
  }

  /**
   * Adds a listener to the list that is notified each time a change to the data model occurs.
   *
   * @param l
   *          the TableModelListener
   */
  public void addTableModelListener( final TableModelListener l ) {
    // ignored
  }

  /**
   * Removes a listener from the list that is notified each time a change to the data model occurs.
   *
   * @param l
   *          the TableModelListener
   */
  public void removeTableModelListener( final TableModelListener l ) {
    // ignored
  }
}
