package org.pentaho.reporting.libraries.parameter;

public interface ParameterData
{
  public String[] getAvailableColumns();
  public boolean isAvailable (String parameterName);
  public Object get (String parameterName);
  public void put (String parameterName, Object value);
}
