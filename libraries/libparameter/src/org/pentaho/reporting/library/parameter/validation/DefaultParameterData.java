package org.pentaho.reporting.library.parameter.validation;

import java.util.HashMap;

import org.pentaho.reporting.library.parameter.ParameterData;

public class DefaultParameterData implements ParameterData
{
  private HashMap<String, Object> values;

  public DefaultParameterData()
  {
    values = new HashMap<String, Object>();
  }

  public DefaultParameterData (final ParameterData parent)
  {
    this();
    final String[] columns = parent.getAvailableColumns();
    for (int i = 0; i < columns.length; i++)
    {
      final String column = columns[i];
      put (column, parent.get(column));
    }
  }

  public String[] getAvailableColumns()
  {
    return values.keySet().toArray(new String[values.size()]);
  }

  public boolean isAvailable(final String parameterName)
  {
    return values.containsKey(parameterName);
  }

  public void put(final String parameterName, final Object value)
  {
    values.put (parameterName, value);
  }

  public Object get(final String parameterName)
  {
    return values.get(parameterName);
  }
}
