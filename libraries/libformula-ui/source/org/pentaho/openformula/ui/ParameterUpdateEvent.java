package org.pentaho.openformula.ui;

import java.util.EventObject;

public class ParameterUpdateEvent extends EventObject
{
  private int parameter;
  private String text;
  private boolean catchAllParameter;

  public ParameterUpdateEvent(final Object source,
                              final int parameter,
                              final String text,
                              final boolean catchAllParameter)
  {
    super(source);
    this.parameter = parameter;
    this.text = text;
    this.catchAllParameter = catchAllParameter;
  }

  public int getParameter()
  {
    return parameter;
  }

  public String getText()
  {
    return text;
  }

  public boolean isCatchAllParameter()
  {
    return catchAllParameter;
  }
}
