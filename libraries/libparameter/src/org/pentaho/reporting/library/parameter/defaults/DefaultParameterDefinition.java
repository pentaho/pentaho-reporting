package org.pentaho.reporting.library.parameter.defaults;

import java.util.ArrayList;

import org.pentaho.reporting.library.parameter.Parameter;
import org.pentaho.reporting.library.parameter.ParameterDefinition;
import org.pentaho.reporting.library.parameter.ParameterValidator;
import org.pentaho.reporting.library.parameter.validation.DefaultParameterValidator;

public class DefaultParameterDefinition implements ParameterDefinition
{
  private ArrayList<Parameter> parameters;
  private ParameterValidator parameterValidator;

  public DefaultParameterDefinition()
  {
    parameters = new ArrayList<Parameter>();
    parameterValidator = new DefaultParameterValidator();
  }

  public boolean addParameter(final Parameter parameter)
  {
    return parameters.add(parameter);
  }

  public void addParameter(final int index, final Parameter element)
  {
    parameters.add(index, element);
  }

  public void clear()
  {
    parameters.clear();
  }

  public Parameter removeParameter(final int index)
  {
    return parameters.remove(index);
  }

  public DefaultParameterDefinition clone()
  {
    try
    {
      final DefaultParameterDefinition d = (DefaultParameterDefinition) super.clone();
      d.parameters = (ArrayList<Parameter>) parameters.clone();
      for (int i = 0; i < parameters.size(); i++)
      {
        final Parameter parameter = parameters.get(i);
        d.parameters.set(i, parameter.clone());
      }
      return d;
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException(e);
    }
  }

  public int getParameterCount()
  {
    return parameters.size();
  }

  public Parameter[] getParameterDefinitions()
  {
    return parameters.toArray(new Parameter[parameters.size()]);
  }

  public Parameter getParameterDefinition(final int parameter)
  {
    return parameters.get(parameter);
  }

  public void setParameterValidator(final ParameterValidator parameterValidator)
  {
    if (parameterValidator == null)
    {
      throw new NullPointerException();
    }
    this.parameterValidator = parameterValidator;
  }

  public ParameterValidator getValidator()
  {
    return parameterValidator;
  }
}
