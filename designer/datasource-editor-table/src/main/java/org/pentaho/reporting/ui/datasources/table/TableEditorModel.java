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

package org.pentaho.reporting.ui.datasources.table;

import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;

public class TableEditorModel extends AbstractTableModel {
  private static final int COL_INCREMENT = 50;

  private boolean suspendEvents;
  private ArrayList<String> columnNames;
  private ArrayList<Class> columnTypes;
  private ArrayList<ArrayList<Object>> data;

  /**
   * Constructs a default <code>DefaultTableModel</code> which is a table of zero columns and zero rows.
   */
  public TableEditorModel() {
    this.columnNames = new ArrayList<String>( COL_INCREMENT );
    this.columnTypes = new ArrayList<Class>( COL_INCREMENT );
    this.data = new ArrayList<ArrayList<Object>>( 1000 );
  }

  public void setColumnName( final int index, final String identifier ) {
    if ( index == 0 ) {
      throw new IllegalStateException();
    }

    columnNames.set( index - 1, identifier );
    fireTableStructureChanged();
  }

  public void removeColumn( final int column ) {
    final int size = data.size();
    for ( int rowIndex = 0; rowIndex < size; rowIndex++ ) {
      final ArrayList theRow = data.get( rowIndex );
      theRow.remove( column );
    }

    columnNames.remove( column );
    columnTypes.remove( column );

    fireTableStructureChanged();
  }

  public void removeRow( final int row ) {
    data.remove( row );
  }

  public void addColumn( final String name, final Class type ) {
    columnNames.add( name );
    columnTypes.add( type );

    final int colCount = getColumnCount();
    for ( int theRowIndex = 0; theRowIndex < data.size(); theRowIndex++ ) {
      final ArrayList<Object> row = data.get( theRowIndex );
      while ( row.size() < colCount ) {
        row.add( null );
      }
    }

    fireTableStructureChanged();
  }

  public void addRow() {
    final int colCount = columnNames.size();
    final ArrayList<Object> newRow = new ArrayList<Object>( colCount );
    for ( int i = 0; i < colCount; i++ ) {
      newRow.add( null );
    }
    data.add( newRow );
    fireTableDataChanged();
  }

  public void addRow( final int idx ) {
    final int colCount = columnNames.size();
    final ArrayList<Object> newRow = new ArrayList<Object>( colCount );
    for ( int i = 0; i < colCount; i++ ) {
      newRow.add( null );
    }
    data.add( idx, newRow );
    fireTableDataChanged();
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    if ( column == 0 ) {
      return "#";
    }
    return columnNames.get( column - 1 );
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  public Class<?> getColumnClass( final int columnIndex ) {
    if ( columnIndex == 0 ) {
      return Integer.class;
    }
    return columnTypes.get( columnIndex - 1 );
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return data.size();
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    if ( columnNames.isEmpty() ) {
      return 0;
    }

    return 1 + columnNames.size();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    if ( columnIndex == 0 ) {
      return Integer.valueOf( rowIndex + 1 );
    }
    final ArrayList<Object> list = data.get( rowIndex );
    return list.get( columnIndex - 1 );
  }

  /**
   * Returns false.  This is the default implementation for all cells.
   *
   * @param rowIndex    the row being queried
   * @param columnIndex the column being queried
   * @return false
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return columnIndex != 0;
  }

  /**
   * This empty implementation is provided so users don't have to implement this method if their data model is not
   * editable.
   *
   * @param aValue      value to assign to cell
   * @param rowIndex    row of cell
   * @param columnIndex column of cell
   */
  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    if ( columnIndex == 0 ) {
      throw new IllegalStateException();
    }
    final ArrayList<Object> list = data.get( rowIndex );
    list.set( columnIndex - 1, aValue );
    fireTableCellUpdated( rowIndex, columnIndex - 1 );
  }

  public void clear() {
    data.clear();
    columnNames.clear();
    columnTypes.clear();
    fireTableStructureChanged();
  }

  public void setColumnClass( final int index, final Class type ) {
    if ( index == 0 ) {
      return;
    }
    if ( type == null ) {
      throw new NullPointerException();
    }
    final int realIndex = index - 1;
    columnTypes.set( realIndex, type );
    for ( int rowIndex = 0; rowIndex < getRowCount(); rowIndex++ ) {
      final Object currentValue = getValueAt( rowIndex, index );
      final Object newValue = ConverterRegistry.convert( currentValue, type, null );
      setValueAt( newValue, rowIndex, index );
    }
    fireTableStructureChanged();
  }


  /**
   * Notifies all listeners that all cell values in the table's rows may have changed. The number of rows may also have
   * changed and the <code>JTable</code> should redraw the table from scratch. The structure of the table (as in the
   * order of the columns) is assumed to be the same.
   *
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   * @see javax.swing.JTable#tableChanged(javax.swing.event.TableModelEvent)
   */
  public void fireTableDataChanged() {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableDataChanged();
  }

  /**
   * Notifies all listeners that the table's structure has changed. The number of columns in the table, and the names
   * and types of the new columns may be different from the previous state. If the <code>JTable</code> receives this
   * event and its <code>autoCreateColumnsFromModel</code> flag is set it discards any table columns that it had and
   * reallocates default columns in the order they appear in the model. This is the same as calling
   * <code>setModel(TableModel)</code> on the <code>JTable</code>.
   *
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   */
  public void fireTableStructureChanged() {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableStructureChanged();
  }

  /**
   * Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
   *
   * @param firstRow the first row
   * @param lastRow  the last row
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   */
  public void fireTableRowsInserted( final int firstRow, final int lastRow ) {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableRowsInserted( firstRow, lastRow );
  }

  /**
   * Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive, have been updated.
   *
   * @param firstRow the first row
   * @param lastRow  the last row
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   */
  public void fireTableRowsUpdated( final int firstRow, final int lastRow ) {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableRowsUpdated( firstRow, lastRow );
  }

  /**
   * Notifies all listeners that rows in the range <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
   *
   * @param firstRow the first row
   * @param lastRow  the last row
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   */
  public void fireTableRowsDeleted( final int firstRow, final int lastRow ) {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableRowsDeleted( firstRow, lastRow );
  }

  /**
   * Notifies all listeners that the value of the cell at <code>[row, column]</code> has been updated.
   *
   * @param row    row of cell which has been updated
   * @param column column of cell which has been updated
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   */
  public void fireTableCellUpdated( final int row, final int column ) {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableCellUpdated( row, column );
  }

  /**
   * Forwards the given notification event to all <code>TableModelListeners</code> that registered themselves as
   * listeners for this table model.
   *
   * @param e the event to be forwarded
   * @see javax.swing.event.TableModelEvent
   * @see javax.swing.event.EventListenerList
   */
  public void fireTableChanged( final TableModelEvent e ) {
    if ( suspendEvents ) {
      return;
    }
    super.fireTableChanged( e );
  }

  public boolean isSuspendEvents() {
    return suspendEvents;
  }

  public void setSuspendEvents( final boolean suspendEvents ) {
    this.suspendEvents = suspendEvents;
    if ( suspendEvents == false ) {
      fireTableStructureChanged();
    }
  }

  public void copyInto( final TableModel model ) {
    try {
      setSuspendEvents( true );
      clear();
      if ( model == null ) {
        return;
      }

      final int columnCount = model.getColumnCount();
      for ( int col = 0; col < columnCount; col++ ) {
        addColumn( model.getColumnName( col ), model.getColumnClass( col ) );
      }

      final int rowCount = model.getRowCount();
      for ( int r = 0; r < rowCount; r++ ) {
        addRow();
        for ( int col = 0; col < columnCount; col++ ) {
          final Object originalValue = model.getValueAt( r, col );
          setValueAt( originalValue, r, col + 1 );
        }
      }
    } finally {
      setSuspendEvents( false );
    }
  }

  public TableModel createModel() {
    final TypedTableModel tableModel = new TypedTableModel();
    final int columnCount = getColumnCount();
    for ( int col = 1; col < columnCount; col++ ) {
      tableModel.addColumn( getColumnName( col ), getColumnClass( col ) );
    }

    final int rowCount = getRowCount();
    for ( int r = 0; r < rowCount; r++ ) {
      for ( int col = 1; col < columnCount; col++ ) {
        tableModel.setValueAt( getValueAt( r, col ), r, col - 1 );
      }
    }

    return tableModel;
  }
}
