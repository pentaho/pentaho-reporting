package org.pentaho.reporting.ui.datasources.kettle;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.table.AbstractTableModel;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.bulk.BulkDataProvider;

public class ArgumentTableModel extends AbstractTableModel implements BulkDataProvider
{
  private ArrayList<String> backend;

  public ArgumentTableModel()
  {
    backend = new ArrayList<String>();
  }

  public int getBulkDataSize()
  {
    return getRowCount();
  }

  public Object[] getBulkData()
  {
    return backend.toArray();
  }

  public void setBulkData(final Object[] data)
  {
    backend.clear();
    for (int i = 0; i < data.length; i++)
    {
      backend.add((String) data[i]);
    }
  }

  public String[] getArguments()
  {
    final ArrayList<String> retval = new ArrayList<String>();
    for (int i = 0; i < backend.size(); i++)
    {
      final String s = backend.get(i);
      if (StringUtils.isEmpty(s) == false)
      {
        retval.add(s);
      }
    }

    return retval.toArray(new String[retval.size()]);
  }

  public void setArguments(final String[] args)
  {
    backend.clear();
    backend.addAll(Arrays.asList(args));
    fireTableDataChanged();
  }

  public void addRow()
  {
    backend.add(null);
    fireTableDataChanged();
  }

  public void removeRow(final int index)
  {
    backend.remove(index);
    fireTableDataChanged();
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
   * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
   *
   * @param columnIndex the column being queried
   * @return the Object.class
   */
  public Class getColumnClass(final int columnIndex)
  {
    if (columnIndex == 0)
    {
      return Integer.class;
    }
    return String.class;
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
    return columnIndex == 1;
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
    switch (column)
    {
      case 0:
        return "#";
      case 1:
        return "Column";
      default:
        throw new IndexOutOfBoundsException();
    }
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
    if (columnIndex == 0)
    {
      return Integer.valueOf(rowIndex);
    }
    return backend.get(rowIndex);
  }

  /**
   * This empty implementation is provided so users don't have to implement
   * this method if their data model is not editable.
   *
   * @param aValue      value to assign to cell
   * @param rowIndex    row of cell
   * @param columnIndex column of cell
   */
  public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex)
  {
    if (columnIndex != 1)
    {
      return;
    }
    backend.set(rowIndex, (String) aValue);
    fireTableCellUpdated(rowIndex, columnIndex);
  }
}
