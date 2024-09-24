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

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Date;

public class GeneratorTableModel extends AbstractTableModel {
  private String[] columnNames;
  private Class[] columnTypes;
  private int rowCount;

  public GeneratorTableModel( final String[] columnNames, final Class[] columnTypes, final int rowCount ) {
    this.columnNames = columnNames;
    this.columnTypes = columnTypes;
    this.rowCount = rowCount;
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display. This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return rowCount;
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return columnNames.length;
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
    try {
      final Class colType = getColumnClass( columnIndex );
      if ( Number.class.isAssignableFrom( colType ) ) {
        return ConverterRegistry.toPropertyValue( String.valueOf( ( rowIndex * getColumnCount() ) + columnIndex ),
            colType );
      }
      if ( Date.class.isAssignableFrom( colType ) ) {
        final Date date = new Date( ( ( rowIndex * getColumnCount() ) + columnIndex ) * ( 12 * 60 * 60 * 1000 ) );
        return ConverterRegistry.toPropertyValue( ConverterRegistry.toAttributeValue( date ), colType );
      }
      if ( String.class.isAssignableFrom( colType ) ) {
        return getColumnName( columnIndex ) + rowIndex;
      }
      return null;
    } catch ( Exception e ) {
      return null;
    }
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex
   *          the column being queried
   * @return the Object.class
   */
  public Class getColumnClass( final int columnIndex ) {
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
  public String getColumnName( final int column ) {
    return columnNames[column];
  }

  public static void main( String[] args ) {
    TableModel m =
        new org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.GeneratorTableModel( new String[] { "id",
          "name", "firstname", "zip", "city", "birthdate", "street", "housenr", "statecode", "state" }, new Class[] {
            Integer.class, String.class, String.class, Integer.class, String.class, java.util.Date.class, String.class,
            Integer.class, String.class, String.class }, 400000 );
  }
}
