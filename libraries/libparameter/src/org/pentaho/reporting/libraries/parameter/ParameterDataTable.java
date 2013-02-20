package org.pentaho.reporting.libraries.parameter;

public interface ParameterDataTable
{
  int getRowCount();
  Object getValue (String column, int row);
}
