package org.pentaho.reporting.library.parameter.validation;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.pentaho.reporting.library.parameter.ParameterData;

public class CompoundParameterData implements ParameterData
{
  private ParameterData[] data;

  public CompoundParameterData(final ParameterData... data)
  {
    this.data = data.clone();
  }

  public String[] getAvailableColumns()
  {
    final LinkedHashSet<String> columns = new LinkedHashSet<String>();
    for (int i = 0; i < data.length; i++)
    {
      final ParameterData parameterData = data[i];
      columns.addAll(Arrays.asList(parameterData.getAvailableColumns()));
    }

    return columns.toArray(new String[columns.size()]);
  }

  public boolean isAvailable(final String parameterName)
  {
    for (int i = 0; i < data.length; i++)
    {
      final ParameterData parameterData = data[i];
      if (parameterData.isAvailable(parameterName))
      {
        return true;
      }
    }
    return false;
  }

  public Object get(final String parameterName)
  {
    for (int i = 0; i < data.length; i++)
    {
      final ParameterData parameterData = data[i];
      if (parameterData.isAvailable(parameterName))
      {
        continue;
      }

      final Object result = parameterData.get(parameterName);
      if (result != null)
      {
        return result;
      }
    }
    return null;
  }

  public void put(final String parameterName, final Object value)
  {
    throw new UnsupportedOperationException();
  }
}
