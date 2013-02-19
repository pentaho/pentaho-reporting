package org.pentaho.reporting.library.parameter.defaults;

import org.pentaho.reporting.library.parameter.ListParameter;
import org.pentaho.reporting.library.parameter.Parameter;
import org.pentaho.reporting.library.parameter.ParameterDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ParameterParser
{
  public ParameterParser()
  {
  }

  public ParameterDefinition parseDefinition (Element node)
  {
    DefaultParameterDefinition retval = new DefaultParameterDefinition();
    final NodeList childNodes = node.getChildNodes();

    return null;
  }

  public Parameter parsePlainParameter (Element node)
  {
    return null;
  }

  public ListParameter parseListParameter (Element node)
  {
    return null;
  }
}
