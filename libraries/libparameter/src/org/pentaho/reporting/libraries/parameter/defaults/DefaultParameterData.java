package org.pentaho.reporting.libraries.parameter.defaults;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.pentaho.reporting.libraries.parameter.ParameterData;

public class DefaultParameterData implements ParameterData, Serializable
{
  private LinkedHashMap<String, Object> backend;

  public DefaultParameterData()
  {
    backend = new LinkedHashMap<String, Object>();
  }

  public String[] getAvailableColumns()
  {
    return backend.keySet().toArray(new String[backend.size()]);
  }

  public boolean isAvailable(final String parameterName)
  {
    return backend.containsKey(parameterName);
  }

  public Object get(final String parameterName)
  {
    return backend.get(parameterName);
  }

  public void put(final String parameterName, final Object value)
  {
    backend.put (parameterName, value);
  }
}
