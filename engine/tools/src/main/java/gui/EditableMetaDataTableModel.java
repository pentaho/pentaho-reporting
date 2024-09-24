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

package gui;

import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.util.Locale;

public class EditableMetaDataTableModel extends AbstractTableModel {
  private EditableMetaData[] backend;
  private Locale locale;

  public EditableMetaDataTableModel() {
    locale = Locale.getDefault();
    backend = new EditableMetaData[ 0 ];
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }
    this.locale = locale;
    fireTableDataChanged();
  }

  public void populate( EditableMetaData[] data ) {
    backend = data.clone();
    fireTableDataChanged();
  }

  public EditableMetaData getMetaData( int row ) {
    return backend[ row ];
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return backend.length;
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return 8;
  }

  /**
   * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
   * <code>column</code> cannot be found, returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName( final int column ) {
    switch( column ) {
      case 0:
        return "ID";
      case 1:
        return "display-name";
      case 2:
        return "grouping";
      case 3:
        return "grouping.ordinal";
      case 4:
        return "ordinal";
      case 5:
        return "description";
      case 6:
        return "deprecated";
      case 7:
        return "status";
    }
    throw new IndexOutOfBoundsException();
  }

  /**
   * Returns false.  This is the default implementation for all cells.
   *
   * @param rowIndex    the row being queried
   * @param columnIndex the column being queried
   * @return false
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    return columnIndex != 0 && columnIndex != 7;
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  public Class getColumnClass( final int columnIndex ) {
    return String.class;
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final EditableMetaData abstractMetaData = getMetaData( rowIndex );
    switch( columnIndex ) {
      case 0: {
        return abstractMetaData.getName();
      }
      case 1:
        return abstractMetaData.getMetaAttribute( "display-name", locale );
      case 2:
        return abstractMetaData.getMetaAttribute( "grouping", locale );
      case 3:
        return abstractMetaData.getMetaAttribute( "grouping.ordinal", locale );
      case 4:
        return abstractMetaData.getMetaAttribute( "ordinal", locale );
      case 5:
        return abstractMetaData.getMetaAttribute( "description", locale );
      case 6:
        return abstractMetaData.getMetaAttribute( "deprecated", locale );
      case 7:
        String result = "";
        if ( abstractMetaData.isValid( locale, false ) == false ) {
          result += "error ";
        } else if ( abstractMetaData.isValid( locale, true ) == false ) {
          result += "child-error ";
        }
        if ( abstractMetaData.isModified() ) {
          result += "modified";
        }
        return result;
    }
    return null;
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
    final EditableMetaData abstractMetaData = getMetaData( rowIndex );
    final String sValue;
    if ( aValue == null ) {
      sValue = null;
    } else {
      final String source = String.valueOf( aValue );
      if ( StringUtils.isEmpty( source ) ) {
        sValue = null;
      } else {
        sValue = source;
      }
    }
    switch( columnIndex ) {
      case 0: {
        return;
      }
      case 1:
        abstractMetaData.setMetaAttribute( "display-name", locale, sValue );
        fireTableCellUpdated( rowIndex, columnIndex );
        fireTableCellUpdated( rowIndex, 7 );
        return;
      case 2:
        abstractMetaData.setMetaAttribute( "grouping", locale, sValue );
        fireTableCellUpdated( rowIndex, columnIndex );
        fireTableCellUpdated( rowIndex, 7 );
        return;
      case 3:
        abstractMetaData.setMetaAttribute( "grouping.ordinal", locale, sValue );
        fireTableCellUpdated( rowIndex, columnIndex );
        fireTableCellUpdated( rowIndex, 7 );
        return;
      case 4:
        abstractMetaData.setMetaAttribute( "ordinal", locale, sValue );
        fireTableCellUpdated( rowIndex, columnIndex );
        fireTableCellUpdated( rowIndex, 7 );
        return;
      case 5:
        abstractMetaData.setMetaAttribute( "description", locale, sValue );
        fireTableCellUpdated( rowIndex, columnIndex );
        fireTableCellUpdated( rowIndex, 7 );
        return;
      case 6:
        abstractMetaData.setMetaAttribute( "deprecated", locale, sValue );
        fireTableCellUpdated( rowIndex, columnIndex );
        fireTableCellUpdated( rowIndex, 7 );
        return;
    }

  }

  public boolean isValidValue( final int row, final int col ) {
    final EditableMetaData abstractMetaData = getMetaData( row );
    switch( col ) {
      case 0:
        return true;
      case 1:
        return abstractMetaData.isValidValue( "display-name", locale );
      case 2:
        return abstractMetaData.isValidValue( "grouping", locale );
      case 3:
        return abstractMetaData.isValidValue( "grouping.ordinal", locale );
      case 4:
        return abstractMetaData.isValidValue( "ordinal", locale );
      case 5:
        return abstractMetaData.isValidValue( "description", locale );
      case 6:
        return abstractMetaData.isValidValue( "deprecated", locale );
      case 7:
        return true;
    }
    return false;
  }
}
