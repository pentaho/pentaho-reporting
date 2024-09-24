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

package org.pentaho.reporting.ui.datasources.cda;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.table.AbstractTableModel;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class ParameterMappingTableModel extends AbstractTableModel
{
  private ArrayList<ParameterMapping> backend;

  public ParameterMappingTableModel()
  {
    backend = new ArrayList<ParameterMapping>();
  }

  public ParameterMapping[] getMappings()
  {
    final ArrayList<ParameterMapping> list = new ArrayList<ParameterMapping>(backend.size());
    for (int i = 0; i < backend.size(); i++)
    {
      final ParameterMapping mapping = backend.get(i);
      final String sourceColumn = mapping.getName();
      final String alias = mapping.getAlias();
      if (StringUtils.isEmpty(sourceColumn) == false)
      {
        if (StringUtils.isEmpty(alias))
        {
          list.add(new ParameterMapping(sourceColumn, sourceColumn));
        }
        else
        {
          list.add(new ParameterMapping(sourceColumn, alias));
        }
      }
    }

    return list.toArray(new ParameterMapping[list.size()]);
  }

  public void setMappings(final ParameterMapping[] mappings)
  {
    backend.clear();
    backend.addAll(Arrays.asList(mappings));
    fireTableDataChanged();
  }

  public void addRow()
  {
    backend.add(new ParameterMapping("", ""));
    fireTableDataChanged();
  }

  public void removeRow(final int row)
  {
    backend.remove(row);
    fireTableRowsDeleted(row, row);
  }

  /**
   * Returns a default name for the column using spreadsheet conventions:
   * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
   * returns an empty string.
   *
   * @param columnIndex the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  @Override
  public String getColumnName(final int columnIndex)
  {
    switch (columnIndex)
    {
      case 0:
        return Messages.getString("ParameterMappingTableModel.DataRowColumn");
      case 1:
        return Messages.getString("ParameterMappingTableModel.TranformationParameter");
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  /**
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  @Override
  public Class getColumnClass(final int columnIndex)
  {
    return String.class;
  }

  /**
   * Returns false.  This is the default implementation for all cells.
   *
   * @param rowIndex    the row being queried
   * @param columnIndex the column being queried
   * @return false
   */
  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    return true;
  }

  /**
   * Returns the number of rows in the model. A
   * <code>JTable</code> uses this method to determine how many rows it
   * should display.  This method should be quick, as it
   * is called frequently during rendering.
   *
   * @return the number of rows in the model
   * @see #getColumnCount
   */
  public int getRowCount()
  {
    return backend.size();
  }

  /**
   * Returns the number of columns in the model. A
   * <code>JTable</code> uses this method to determine how many columns it
   * should create and display by default.
   *
   * @return the number of columns in the model
   * @see #getRowCount
   */
  public int getColumnCount()
  {
    return 2;
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param rowIndex    the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt(final int rowIndex, final int columnIndex)
  {
    final ParameterMapping mapping = backend.get(rowIndex);
    switch (columnIndex)
    {
      case 0:
        return mapping.getName();
      case 1:
        return mapping.getAlias();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  /**
   * This empty implementation is provided so users don't have to implement
   * this method if their data model is not editable.
   *
   * @param aValue      value to assign to cell
   * @param rowIndex    row of cell
   * @param columnIndex column of cell
   */
  @Override
  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    final ParameterMapping mapping = backend.get(rowIndex);
    switch (columnIndex)
    {
      case 0:
      {
        final String name;
        if (aValue == null)
        {
          name = "";
        }
        else
        {
          name = (String) aValue;
        }
        final String alias = mapping.getAlias();
        backend.set(rowIndex, new ParameterMapping(name, alias));
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      }
      case 1:
      {
        final String name = mapping.getName();
        final String alias;
        if (aValue == null)
        {
          alias = "";
        }
        else
        {
          alias = (String) aValue;
        }
        backend.set(rowIndex, new ParameterMapping(name, alias));
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }
  }
}
