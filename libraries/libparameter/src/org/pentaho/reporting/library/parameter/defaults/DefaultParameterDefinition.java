package org.pentaho.reporting.library.parameter.defaults;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.library.parameter.LibParameterBoot;
import org.pentaho.reporting.library.parameter.ListParameter;
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

  public String toXml () throws IOException
  {
    final StringWriter bout = new StringWriter();
    final XmlWriter writer = new XmlWriter(bout);
    toXml(writer);
    return bout.toString();
  }

  public void toXml(final XmlWriter writer) throws IOException
  {
    writer.writeTag(LibParameterBoot.NAMESPACE, "parameter-definition", XmlWriter.OPEN);

    final Parameter[] parameterDefinitions = getParameterDefinitions();
    for (int i = 0; i < parameterDefinitions.length; i++)
    {
      final Parameter entry = parameterDefinitions[i];
      entry.toXml(writer);
    }

    writer.writeCloseTag();
  }

}
