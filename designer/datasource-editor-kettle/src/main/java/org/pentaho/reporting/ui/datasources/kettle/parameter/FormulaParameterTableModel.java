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

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import org.pentaho.reporting.libraries.designtime.swing.table.GroupingHeader;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTableModel;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyEditor;

public class FormulaParameterTableModel extends AbstractTableModel implements PropertyTableModel {
  private static final FormulaParameterEntity[] EMPTY_ELEMENTS = new FormulaParameterEntity[ 0 ];

  private FormulaParameterEntity[] elements;

  /**
   * Constructs a default <code>DefaultTableModel</code> which is a table of zero columns and zero rows.
   */
  public FormulaParameterTableModel() {
    this.elements = EMPTY_ELEMENTS;
  }

  /**
   * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount() {
    return elements.length;
  }

  /**
   * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount() {
    return 2;
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
      return Messages.getInstance().getString( "FormulaParameterTableModel.Name" );
    }
    return Messages.getInstance().getString( "FormulaParameterTableModel.Value" );
  }

  protected void updateData( final FormulaParameterEntity[] elements ) {
    this.elements = elements.clone();

    fireTableDataChanged();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    final FormulaParameterEntity metaData = elements[ rowIndex ];
    if ( metaData == null ) {
      return elements[ rowIndex ];
    }

    switch( columnIndex ) {
      case 0:
        return metaData;
      case 1:
        return metaData.getValue();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  /**
   * Returns false.  This is the default implementation for all cells.
   *
   * @param rowIndex    the row being queried
   * @param columnIndex the column being queried
   * @return false
   */
  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final FormulaParameterEntity metaData = elements[ rowIndex ];
    if ( metaData == null ) {
      return false;
    }

    switch( columnIndex ) {
      case 0:
        return metaData.getType() == FormulaParameterEntity.Type.PARAMETER;
      case 1:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final FormulaParameterEntity metaData = elements[ rowIndex ];
    if ( metaData == null ) {
      return;
    }

    switch( columnIndex ) {
      case 0:
        if ( aValue instanceof FormulaParameterEntity ) {
          final FormulaParameterEntity name = (FormulaParameterEntity) aValue;
          metaData.setName( name.getName() );
          fireTableDataChanged();
        }
        return;
      case 1: {
        if ( aValue == null ) {
          metaData.setValue( null );
        } else {
          metaData.setValue( String.valueOf( aValue ) );
        }
        fireTableDataChanged();
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  public Class getClassForCell( final int row, final int column ) {
    final FormulaParameterEntity metaData = elements[ row ];
    if ( metaData == null ) {
      return GroupingHeader.class;
    }

    if ( column == 0 ) {
      return FormulaParameterEntity.class;
    }

    return String.class;
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    return null;
  }

  public void setData( final FormulaParameterEntity[] parameter ) {
    updateData( parameter );
  }

  public FormulaParameterEntity[] getData() {
    return elements.clone();
  }

  public FormulaParameterEntity.Type getParameterType( final int row ) {
    final FormulaParameterEntity downParameter = elements[ row ];
    if ( downParameter != null ) {
      return downParameter.getType();
    }
    return null;
  }
}
