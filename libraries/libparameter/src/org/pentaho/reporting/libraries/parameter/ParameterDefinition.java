package org.pentaho.reporting.libraries.parameter;

public interface ParameterDefinition
{
  public Object clone();

  public int getParameterCount();

  public Parameter[] getParameterDefinitions();

  public Parameter getParameterDefinition(int parameter);

  public ParameterValidator getValidator();
}
