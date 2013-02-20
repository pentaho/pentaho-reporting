package org.pentaho.reporting.libraries.parameter.defaults;

import org.pentaho.reporting.libraries.parameter.ParameterData;
import org.pentaho.reporting.libraries.parameter.ParameterDataTable;
import org.pentaho.reporting.libraries.parameter.ParameterQuery;

public class EmptyParameterQuery implements ParameterQuery
{
  public EmptyParameterQuery()
  {
  }

  public ParameterDataTable performQuery(final String queryIdentifier, final ParameterData parameterData)
  {
    return new EmptyParameterDataTable();
  }
}
