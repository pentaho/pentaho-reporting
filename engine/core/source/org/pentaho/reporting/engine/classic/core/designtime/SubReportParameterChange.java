package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;

public class SubReportParameterChange implements Change
{
  public enum Type { INPUT, EXPORT}
  private ParameterMapping[] oldValue;
  private ParameterMapping[] newValue;
  private Type type;

  public SubReportParameterChange(final Type type,
                                  final ParameterMapping[] oldValue,
                                  final ParameterMapping[] newValue)
  {
    this.type = type;
    this.oldValue = oldValue.clone();
    this.newValue = newValue.clone();
  }

  public Type getType()
  {
    return type;
  }

  public ParameterMapping[] getOldValue()
  {
    return oldValue.clone();
  }

  public ParameterMapping[] getNewValue()
  {
    return newValue.clone();
  }
}
