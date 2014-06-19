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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import javax.swing.table.AbstractTableModel;

import org.pentaho.reporting.libraries.designtime.swing.table.GroupingHeader;
import org.pentaho.reporting.libraries.designtime.swing.table.GroupingModel;
import org.pentaho.reporting.libraries.designtime.swing.table.SortableTableModel;
import org.pentaho.reporting.libraries.designtime.swing.table.TableStyle;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTableModel;

public class FormulaParameterTableModel extends AbstractTableModel
    implements PropertyTableModel, GroupingModel, SortableTableModel
{
  private static class PlainParameterComparator implements Comparator<FormulaParameterEntity>
  {
    public int compare(final FormulaParameterEntity parameter1, final FormulaParameterEntity parameter2)
    {
      if (parameter1 == null && parameter2 == null)
      {
        return 0;
      }
      if (parameter1 == null)
      {
        return -1;
      }
      if (parameter2 == null)
      {
        return 1;
      }

      return parameter1.getName().compareTo(parameter2.getName());
    }
  }

  private static class GroupedParameterComparator implements Comparator<FormulaParameterEntity>
  {
    public int compare(final FormulaParameterEntity parameter1, final FormulaParameterEntity parameter2)
    {
      if (parameter1 == null && parameter2 == null)
      {
        return 0;
      }
      if (parameter1 == null)
      {
        return -1;
      }
      if (parameter2 == null)
      {
        return 1;
      }
      final FormulaParameterEntity.Type type1 = parameter1.getType();
      final FormulaParameterEntity.Type type2 = parameter2.getType();
      final int compareType = type1.compareTo(type2);
      if (compareType != 0)
      {
        return compareType;
      }
      return parameter1.getName().compareTo(parameter2.getName());
    }
  }

  private static final FormulaParameterEntity[] EMPTY_ELEMENTS = new FormulaParameterEntity[0];
  private static final GroupingHeader[] EMPTY_GROUPINGS = new GroupingHeader[0];

  private HashSet<String> filteredParameterNames;
  private String[] filteredParameterNamesArray;
  
  private GroupingHeader[] groupings;
  private TableStyle tableStyle;
  private FormulaParameterEntity[] elements;
  private FormulaParameterEntity[] groupedElements;

  /**
   * Constructs a default <code>DefaultTableModel</code>
   * which is a table of zero columns and zero rows.
   */
  public FormulaParameterTableModel()
  {
    this.filteredParameterNamesArray = new String[0];
    this.filteredParameterNames = new HashSet<String>();
    this.tableStyle = TableStyle.GROUPED;
    this.elements = EMPTY_ELEMENTS;
    this.groupings = EMPTY_GROUPINGS;
    this.groupedElements = EMPTY_ELEMENTS;
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
    return groupedElements.length;
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
   * Returns a default name for the column using spreadsheet conventions:
   * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
   * returns an empty string.
   *
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName(final int column)
  {
    if (column == 0)
    {
      return Messages.getInstance().getString("FormulaParameterTableModel.Name");
    }
    return Messages.getInstance().getString("FormulaParameterTableModel.Value");
  }


  public TableStyle getTableStyle()
  {
    return tableStyle;
  }

  public void setTableStyle(final TableStyle tableStyle)
  {
    if (tableStyle == null)
    {
      throw new NullPointerException();
    }
    this.tableStyle = tableStyle;
    updateData(getData());
  }

  private FormulaParameterEntity[] filter(final FormulaParameterEntity[] elements)
  {
    final ArrayList<FormulaParameterEntity> retval = new ArrayList<FormulaParameterEntity>(elements.length);
    for (int i = 0; i < elements.length; i++)
    {
      final FormulaParameterEntity element = elements[i];
      if (filteredParameterNames.contains(element.getName()))
      {
        continue;
      }
      retval.add(element);
    }
    return retval.toArray(new FormulaParameterEntity[retval.size()]);
  }

  protected void updateData(final FormulaParameterEntity[] elements)
  {
    this.elements = elements.clone();

    final FormulaParameterEntity[] metaData = filter(elements);
    if (tableStyle == TableStyle.ASCENDING)
    {
      Arrays.sort(metaData, new PlainParameterComparator());
      this.groupings = new GroupingHeader[metaData.length];
      this.groupedElements = metaData;
    }
    else if (tableStyle == TableStyle.DESCENDING)
    {
      Arrays.sort(metaData, Collections.reverseOrder(new PlainParameterComparator()));
      this.groupings = new GroupingHeader[metaData.length];
      this.groupedElements = metaData;
    }
    else
    {
      Arrays.sort(metaData, new GroupedParameterComparator());

      int groupCount = 0;
      if (metaData.length > 0)
      {
        FormulaParameterEntity.Type oldValue = null;

        for (int i = 0; i < metaData.length; i++)
        {
          if (groupCount == 0)
          {
            groupCount = 1;
            final FormulaParameterEntity firstdata = metaData[i];
            oldValue = firstdata.getType();
            continue;
          }

          final FormulaParameterEntity data = metaData[i];
          final FormulaParameterEntity.Type grouping = data.getType();
          if ((ObjectUtilities.equal(oldValue, grouping)) == false)
          {
            oldValue = grouping;
            groupCount += 1;
          }
        }
      }

      final FormulaParameterEntity[] groupedMetaData = new FormulaParameterEntity[metaData.length + groupCount];
      this.groupings = new GroupingHeader[groupedMetaData.length];
      int targetIdx = 0;
      GroupingHeader group = null;
      for (int sourceIdx = 0; sourceIdx < metaData.length; sourceIdx++)
      {
        final FormulaParameterEntity data = metaData[sourceIdx];
        if (sourceIdx == 0)
        {
          group = new GroupingHeader(data.getType().toString());
          groupings[targetIdx] = group;
          targetIdx += 1;
        }
        else
        {
          final String newgroup = data.getType().toString();
          if ((ObjectUtilities.equal(newgroup, group.getHeaderText())) == false)
          {
            group = new GroupingHeader(newgroup);
            groupings[targetIdx] = group;
            targetIdx += 1;
          }
        }

        groupings[targetIdx] = group;
        groupedMetaData[targetIdx] = data;
        targetIdx += 1;
      }
      this.groupedElements = groupedMetaData;
    }

    fireTableDataChanged();
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
    final FormulaParameterEntity metaData = groupedElements[rowIndex];
    if (metaData == null)
    {
      return groupings[rowIndex];
    }

    switch (columnIndex)
    {
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
  public boolean isCellEditable(final int rowIndex, final int columnIndex)
  {
    final FormulaParameterEntity metaData = groupedElements[rowIndex];
    if (metaData == null)
    {
      return false;
    }

    switch (columnIndex)
    {
      case 0:
        return metaData.getType() == FormulaParameterEntity.Type.ARGUMENT;
      case 1:
        return true;
      default:
        throw new IndexOutOfBoundsException();
    }
  }


  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    final FormulaParameterEntity metaData = groupedElements[rowIndex];
    if (metaData == null)
    {
      return;
    }

    switch (columnIndex)
    {
      case 0:
        if (aValue instanceof FormulaParameterEntity)
        {
          final FormulaParameterEntity name = (FormulaParameterEntity) aValue;
          metaData.setName(name.getName());
          fireTableDataChanged();
        }
        return;
      case 1:
      {
        if (aValue == null)
        {
          metaData.setValue(null);
        }
        else
        {
          metaData.setValue(String.valueOf(aValue));
        }
        fireTableDataChanged();
        break;
      }
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  public Class getClassForCell(final int row, final int column)
  {
    final FormulaParameterEntity metaData = groupedElements[row];
    if (metaData == null)
    {
      return GroupingHeader.class;
    }

    if (column == 0)
    {
      return FormulaParameterEntity.class;
    }

    return String.class;
  }

  public PropertyEditor getEditorForCell(final int row, final int column)
  {
    return null;
  }

  public GroupingHeader getGroupHeader(final int index)
  {
    return groupings[index];
  }

  public boolean isHeaderRow(final int index)
  {
    return groupedElements[index] == null;
  }

  public String[] getFilteredParameterNames()
  {
    return filteredParameterNamesArray.clone();
  }

  public void setFilteredParameterNames(final String[] names)
  {
    this.filteredParameterNamesArray = names.clone();
    this.filteredParameterNames.clear();
    this.filteredParameterNames.addAll(Arrays.asList(names));

    updateData(elements);
  }

  public void setData(final FormulaParameterEntity[] parameter)
  {
    updateData(parameter);
  }

  public FormulaParameterEntity[] getData()
  {
    return elements.clone();
  }

  public FormulaParameterEntity[] getGroupedData()
  {
    return groupedElements.clone();
  }

  public FormulaParameterEntity.Type getParameterType(final int row)
  {
    final FormulaParameterEntity downParameter = groupedElements[row];
    if (downParameter != null)
    {
      return downParameter.getType();
    }
    return null;
  }
}
