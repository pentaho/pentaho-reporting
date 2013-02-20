package org.pentaho.reporting.libraries.parameter.defaults;

import java.util.ArrayList;
import java.util.Arrays;

import org.pentaho.reporting.libraries.base.util.GenericObjectTable;
import org.pentaho.reporting.libraries.parameter.ParameterDataTable;

public class DefaultParameterDataTable implements ParameterDataTable
{
  private ArrayList<String> columnNames;
  private GenericObjectTable<Object> backend;

  public DefaultParameterDataTable(String... columnNames)
  {
    this.columnNames = new ArrayList<String>(Arrays.asList(columnNames));
    this.backend = new GenericObjectTable<Object>();
  }

  public int getRowCount()
  {
    return this.backend.getRowCount();
  }

  public String getColumnName (int index)
  {
    return columnNames.get(index);
  }

  public int getColumnCount ()
  {
    return columnNames.size();
  }

  public void addRow (final Object... data)
  {
    final int rowCount = getRowCount();
    final int maxIdx = Math.min(data.length, columnNames.size());
    for (int i = 0; i < maxIdx; i++)
    {
      final Object o = data[i];
      backend.setObject(rowCount, i, o);
    }
  }

  public void setValue(final int column, final int row, final Object value)
  {
    backend.setObject(row, column, value);
  }

  public void setValue(final String column, final int row, final Object value)
  {
    final int idx = columnNames.indexOf(column);
    if (idx == -1)
    {
      throw new IllegalArgumentException();
    }
    backend.setObject(row, idx, value);
  }

  public Object getValue(final String column, final int row)
  {
    final int idx = columnNames.indexOf(column);
    if (idx == -1)
    {
      return null;
    }
    return backend.getObject(row, idx);
  }
}
