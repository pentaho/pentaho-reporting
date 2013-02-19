package org.pentaho.reporting.library.parameter;

public interface ParameterQuery
{
  public ParameterDataTable performQuery(String queryIdentifier, ParameterData parameterData);
}
