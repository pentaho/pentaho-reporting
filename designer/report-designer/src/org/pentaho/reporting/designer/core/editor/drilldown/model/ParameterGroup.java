package org.pentaho.reporting.designer.core.editor.drilldown.model;

import java.util.LinkedHashMap;

/**
 * Todo: Document me!
 * <p/>
 * Date: 22.07.2010
 * Time: 13:51:53
 *
 * @author Thomas Morgner.
 */
public class ParameterGroup
{
  private String name;
  private String label;
  private LinkedHashMap<String, Parameter> parameters;

  public ParameterGroup(final String name, final String parameterGroupLabel)
  {
    this.name = name;
    this.label = parameterGroupLabel;
    this.parameters = new LinkedHashMap<String, Parameter>();
  }

  public String getLabel()
  {
    return label;
  }

  public String getName()
  {
    return name;
  }

  public void addParameter(final Parameter parameter)
  {
    parameters.put(parameter.getName(), parameter);
  }

  public Parameter getParameter(final String parameter)
  {
    return parameters.get(parameter);
  }

  public Parameter[] getParameters()
  {
    return parameters.values().toArray(new Parameter[parameters.size()]);
  }
}
