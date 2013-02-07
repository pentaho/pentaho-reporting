package org.pentaho.reporting.library.parameter;

public interface ParameterDefinition
{
  public Object clone();

  public int getParameterCount();

  public Parameter[] getParameterDefinitions();

  public Parameter getParameterDefinition(int parameter);

  public ParameterValidator getValidator();
}
