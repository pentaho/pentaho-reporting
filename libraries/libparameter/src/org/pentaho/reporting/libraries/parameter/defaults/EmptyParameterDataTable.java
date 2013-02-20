package org.pentaho.reporting.libraries.parameter.defaults;

import org.pentaho.reporting.libraries.parameter.ParameterDataTable;

public class EmptyParameterDataTable implements ParameterDataTable
{
  public EmptyParameterDataTable()
  {
  }

  public int getRowCount()
  {
    return 0;
  }

  public Object getValue(final String column, final int row)
  {
    return null;
  }
}
