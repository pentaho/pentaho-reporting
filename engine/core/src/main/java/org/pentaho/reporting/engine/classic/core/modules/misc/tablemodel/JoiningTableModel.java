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

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;

public class JoiningTableModel extends AbstractTableModel {
  private static class TablePosition {
    private TableModel tableModel;
    private String prefix;
    private int tableOffset;
    private int columnOffset;

    private TablePosition( final TableModel tableModel, final String prefix ) {
      if ( tableModel == null ) {
        throw new NullPointerException( "Model must not be null" ); //$NON-NLS-1$
      }
      if ( prefix == null ) {
        throw new NullPointerException( "Prefix must not be null." ); //$NON-NLS-1$
      }
      this.tableModel = tableModel;
      this.prefix = prefix;
    }

    public void updateOffsets( final int tableOffset, final int columnOffset ) {
      this.tableOffset = tableOffset;
      this.columnOffset = columnOffset;
    }

    public String getPrefix() {
      return prefix;
    }

    public int getColumnOffset() {
      return columnOffset;
    }

    public TableModel getTableModel() {
      return tableModel;
    }

    public int getTableOffset() {
      return tableOffset;
    }
  }

  private class TableChangeHandler implements TableModelListener {
    private TableChangeHandler() {
    }

    /**
     * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed.
     */
    public void tableChanged( final TableModelEvent e ) {
      if ( e.getType() == TableModelEvent.UPDATE && e.getFirstRow() == TableModelEvent.HEADER_ROW ) {
        updateStructure();
      } else if ( e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE ) {
        updateRowCount();
      } else {
        updateData();
      }
    }
  }

  // the column names of all tables ..
  private String[] columnNames;
  // all column types of all tables ..
  private Class[] columnTypes;

  private ArrayList<TablePosition> models;
  private TableChangeHandler changeHandler;
  private int rowCount;
  public static final String TABLE_PREFIX_COLUMN = "TablePrefix"; //$NON-NLS-1$

  public JoiningTableModel() {
    models = new ArrayList<TablePosition>();
    changeHandler = new TableChangeHandler();
  }

  public synchronized void addTableModel( final String prefix, final TableModel model ) {
    models.add( new TablePosition( model, prefix ) );
    model.addTableModelListener( changeHandler );
    updateStructure();
  }

  public synchronized void removeTableModel( final TableModel model ) {
    for ( int i = 0; i < models.size(); i++ ) {
      final TablePosition position = models.get( i );
      if ( position.getTableModel() == model ) {
        models.remove( position );
        model.removeTableModelListener( changeHandler );
        updateStructure();
        return;
      }
    }
  }

  public synchronized int getTableModelCount() {
    return models.size();
  }

  public synchronized TableModel getTableModel( final int pos ) {
    final TablePosition position = models.get( pos );
    return position.getTableModel();
  }

  protected synchronized void updateStructure() {
    final ArrayList<String> columnNames = new ArrayList<String>();
    final ArrayList<Class<?>> columnTypes = new ArrayList<Class<?>>();

    columnNames.add( JoiningTableModel.TABLE_PREFIX_COLUMN );
    columnTypes.add( String.class );

    int columnOffset = 1;
    int rowOffset = 0;
    for ( int i = 0; i < models.size(); i++ ) {
      final TablePosition pos = models.get( i );
      pos.updateOffsets( rowOffset, columnOffset );
      final TableModel tableModel = pos.getTableModel();
      rowOffset += tableModel.getRowCount();
      columnOffset += tableModel.getColumnCount();
      for ( int c = 0; c < tableModel.getColumnCount(); c++ ) {
        columnNames.add( pos.getPrefix() + '.' + tableModel.getColumnName( c ) ); //$NON-NLS-1$
        columnTypes.add( tableModel.getColumnClass( c ) );
      }
    }
    this.columnNames = columnNames.toArray( new String[columnNames.size()] );
    this.columnTypes = columnTypes.toArray( new Class[columnTypes.size()] );
    this.rowCount = rowOffset;
    fireTableStructureChanged();
  }

  protected synchronized void updateRowCount() {
    int rowOffset = 0;
    int columnOffset = 1;
    for ( int i = 0; i < models.size(); i++ ) {
      final TablePosition model = models.get( i );
      model.updateOffsets( rowOffset, columnOffset );
      rowOffset += model.getTableModel().getRowCount();
      columnOffset += model.getTableModel().getColumnCount();
    }
    fireTableStructureChanged();
  }

  protected void updateData() {
    // this is lazy, but we do not optimize for edit-speed here ...
    fireTableDataChanged();
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex
   *          the column being queried
   * @return the Object.class
   */
  public synchronized Class getColumnClass( final int columnIndex ) {
    return columnTypes[columnIndex];
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc. If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column
   *          the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public synchronized String getColumnName( final int column ) {
    return columnNames[column];
  }

  /**
   * Returns false. JFreeReport does not like changing cells.
   *
   * @param rowIndex
   *          the row being queried
   * @param columnIndex
   *          the column being queried
   * @return false
   */
  public final boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return false;
  }

  /**
   * Returns the number of columns managed by the data source object. A <B>JTable</B> uses this method to determine how
   * many columns it should create and display on initialization.
   *
   * @return the number or columns in the model
   * @see #getRowCount
   */
  public synchronized int getColumnCount() {
    return columnNames.length;
  }

  /**
   * Returns the number of records managed by the data source object. A <B>JTable</B> uses this method to determine how
   * many rows it should create and display. This method should be quick, as it is call by <B>JTable</B> quite
   * frequently.
   *
   * @return the number or rows in the model
   * @see #getColumnCount
   */
  public synchronized int getRowCount() {
    return rowCount;
  }

  /**
   * Returns an attribute value for the cell at <I>columnIndex</I> and <I>rowIndex</I>.
   *
   * @param rowIndex
   *          the row whose value is to be looked up
   * @param columnIndex
   *          the column whose value is to be looked up
   * @return the value Object at the specified cell
   */
  public synchronized Object getValueAt( final int rowIndex, final int columnIndex ) {
    // first: find the correct table model...
    final TablePosition pos = getTableModelForRow( rowIndex );
    if ( pos == null ) {
      return null;
    }

    if ( columnIndex == 0 ) {
      return pos.getPrefix();
    }

    final int columnOffset = pos.getColumnOffset();
    if ( columnIndex < columnOffset ) {
      return null;
    }

    final TableModel tableModel = pos.getTableModel();
    if ( columnIndex >= ( columnOffset + tableModel.getColumnCount() ) ) {
      return null;
    }
    return tableModel.getValueAt( rowIndex - pos.getTableOffset(), columnIndex - columnOffset );
  }

  private TablePosition getTableModelForRow( final int row ) {
    // assume, that the models are in ascending order ..
    for ( int i = 0; i < models.size(); i++ ) {
      final TablePosition pos = models.get( i );
      final int maxRow = pos.getTableOffset() + pos.getTableModel().getRowCount();
      if ( row < maxRow ) {
        return pos;
      }
    }
    return null;
  }
}
