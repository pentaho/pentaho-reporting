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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.libraries.base.util.GenericObjectTable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TypedTableModel extends AbstractTableModel {
  private ArrayList<String> columnNames;
  private ArrayList<Class<?>> columnClasses;
  private GenericObjectTable<Object> data;

  public TypedTableModel() {
    this( 10, 10 );
  }

  public TypedTableModel( final int rowIncrement, final int columnIncrement ) {
    data = new GenericObjectTable<Object>( Math.max( 1, rowIncrement ), Math.max( 1, columnIncrement ) );
    columnNames = new ArrayList<String>( columnIncrement );
    columnClasses = new ArrayList<Class<?>>( columnIncrement );
  }

  public TypedTableModel( final String[] columnNames ) {
    this( 10, columnNames.length );
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[i];
      this.columnNames.add( columnName );
      this.columnClasses.add( Object.class );
    }
  }

  public TypedTableModel( final String[] columnNames, final Class<?>[] columnClasses ) {
    this( columnNames, columnClasses, 10 );
  }

  public TypedTableModel( final String[] columnNames, final Class<?>[] columnClasses, final int rowCount ) {
    this( rowCount, columnNames.length );
    if ( columnNames.length != columnClasses.length ) {
      throw new IllegalArgumentException();
    }

    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[i];
      this.columnNames.add( columnName );
      this.columnClasses.add( columnClasses[i] );
    }
  }

  public void addColumn( final String name, final Class<?> type ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( type == null ) {
      throw new NullPointerException();
    }

    this.columnNames.add( name );
    this.columnClasses.add( type );
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display. This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return data.getRowCount();
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return columnNames.size();
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
    return columnNames.get( columnIndex );
  }

  /**
   * Returns the most specific superclass for all the cell values in the column. This is used by the <code>JTable</code>
   * to set up a default renderer and editor for the column.
   *
   * @param columnIndex
   *          the index of the column
   * @return the common ancestor class of the object values in the model.
   */
  public Class<?> getColumnClass( final int columnIndex ) {
    return (Class) columnClasses.get( columnIndex );
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
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return true;
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
    return data.getObject( rowIndex, columnIndex );
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
   */
  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    data.setObject( rowIndex, columnIndex, aValue );
    fireTableCellUpdated( rowIndex, columnIndex );
  }

  public void setColumnType( final int colIndex, final Class<?> type ) {
    if ( type == null ) {
      throw new NullPointerException();
    }
    columnClasses.set( colIndex, type );
    fireTableStructureChanged();
  }

  public void setColumnName( final int colIndex, final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    columnNames.set( colIndex, name );
    fireTableStructureChanged();
  }

  public void addRow( final Object... objects ) {
    final int row = getRowCount();
    if ( objects.length == 0 ) {
      setValueAt( null, row, 0 );
    } else {
      final int maxCols = Math.min( objects.length, getColumnCount() );
      for ( int i = 0; i < maxCols; i++ ) {
        setValueAt( objects[i], row, i );
      }
    }
    fireTableDataChanged();
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "TypedTableModel" );
    sb.append( "{columnNames=" ).append( columnNames );
    sb.append( ", columnClasses=" ).append( columnClasses );
    sb.append( ", rowCount=" ).append( getRowCount() );
    sb.append( '}' );
    return sb.toString();
  }
}
