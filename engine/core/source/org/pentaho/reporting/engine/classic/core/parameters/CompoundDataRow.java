package org.pentaho.reporting.engine.classic.core.parameters;

import java.util.LinkedHashMap;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

public class CompoundDataRow implements DataRow
{
  private LinkedHashMap<String,Boolean> columnSources;
  private DataRow envDataRow;
  private DataRow dataRow;

  public CompoundDataRow(final DataRow envDataRow, final DataRow dataRow)
  {
    this.envDataRow = envDataRow;
    this.dataRow = dataRow;

    columnSources = new LinkedHashMap<String,Boolean>();
    final String[] dataRowNames = dataRow.getColumnNames();
    for (int i = 0; i < dataRowNames.length; i++)
    {
      final String dataRowName = dataRowNames[i];
      columnSources.put(dataRowName, Boolean.TRUE);
    }

    final String[] envRowNames = envDataRow.getColumnNames();
    for (int i = 0; i < envRowNames.length; i++)
    {
      final String dataRowName = envRowNames[i];
      columnSources.put(dataRowName, Boolean.FALSE);
    }
  }

  public DataRow getEnvDataRow()
  {
    return envDataRow;
  }

  public DataRow getDataRow()
  {
    return dataRow;
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col the item index.
   * @return the value.
   */
  public Object get(final String col)
  {
    final Boolean b = columnSources.get(col);
    if (Boolean.FALSE.equals(b))
    {
      return envDataRow.get(col);
    }
    return dataRow.get(col);
  }

  /**
   * Returns the known column names, this data-row understands. The column names may change over time but do not
   * change while a event is processed by a function. The array returned is a copy of the internal data-storage
   * and can be safely modified.
   *
   * @return the column names as array.
   */
  public String[] getColumnNames()
  {
    final LinkedMap columnSources = new LinkedMap();
    final String[] dataRowNames = dataRow.getColumnNames();
    for (int i = 0; i < dataRowNames.length; i++)
    {
      final String dataRowName = dataRowNames[i];
      columnSources.put(dataRowName, Boolean.TRUE);
    }

    final String[] envRowNames = envDataRow.getColumnNames();
    for (int i = 0; i < envRowNames.length; i++)
    {
      final String dataRowName = envRowNames[i];
      columnSources.put(dataRowName, Boolean.FALSE);
    }
    return (String[]) columnSources.keys(new String[columnSources.size()]);
  }

  /**
   * Checks whether the value contained in the column has changed since the last advance-operation.
   *
   * @param name the name of the column.
   * @return true, if the value has changed, false otherwise.
   */
  public boolean isChanged(final String name)
  {
    return false;
  }

  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("CompoundDataRow");
    sb.append("{envDataRow=").append(envDataRow);
    sb.append(", dataRow=").append(dataRow);
    sb.append('}');
    return sb.toString();
  }
}
