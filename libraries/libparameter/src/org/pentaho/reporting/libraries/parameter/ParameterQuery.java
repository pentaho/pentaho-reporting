package org.pentaho.reporting.libraries.parameter;

public interface ParameterQuery
{
  public ParameterDataTable performQuery(String queryIdentifier, ParameterData parameterData) throws ParameterException;
}
