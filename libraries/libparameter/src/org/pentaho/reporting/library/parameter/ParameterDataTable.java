package org.pentaho.reporting.library.parameter;

public interface ParameterDataTable
{
  int getRowCount();
  Object getValue (String column, int row);
}
