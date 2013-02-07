package org.pentaho.reporting.library.parameter;

import javax.swing.table.TableModel;

public interface ParameterQuery
{
  public TableModel performQuery (String queryIdentifier, ParameterData parameterData);
}
