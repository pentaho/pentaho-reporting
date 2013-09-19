package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import java.util.ArrayList;

import org.pentaho.reporting.designer.core.editor.drilldown.model.Parameter;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010
 * Time: 17:34:04
 *
 * @author Thomas Morgner.
 */
public class ParameterReadHandler extends AbstractXmlReadHandler
{
  private Parameter parameter;
  private ArrayList<ParameterAttributeReadHandler> attributeReadHandlers;
  private ParameterValuesReadHandler valuesReadHandler;

  public ParameterReadHandler()
  {
    attributeReadHandlers = new ArrayList<ParameterAttributeReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    final String name = attrs.getValue(getUri(), "name");// NON-NLS
    parameter = new Parameter(name);
    parameter.setMandatory("true".equals(attrs.getValue(getUri(), "is-mandatory")));// NON-NLS
    parameter.setStrict("true".equals(attrs.getValue(getUri(), "is-strict")));// NON-NLS
    parameter.setMultiSelect("true".equals(attrs.getValue(getUri(), "is-multi-select")));// NON-NLS
    parameter.setType(attrs.getValue(getUri(), "type"));// NON-NLS
    parameter.setTimezoneHint(attrs.getValue(getUri(), "timezone-hint"));// NON-NLS
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }

    if ("attribute".equals(tagName))//NON-NLS
    {
      final ParameterAttributeReadHandler readHandler = new ParameterAttributeReadHandler();
      attributeReadHandlers.add(readHandler);
      return readHandler;
    }
    if ("values".equals(tagName))//NON-NLS
    {
      if (valuesReadHandler == null)
      {
        valuesReadHandler = new ParameterValuesReadHandler(parameter.getType());
      }
      return valuesReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    for (int i = 0; i < attributeReadHandlers.size(); i++)
    {
      final ParameterAttributeReadHandler readHandler = attributeReadHandlers.get(i);
      parameter.setAttribute(readHandler.getNamespace(), readHandler.getName(), readHandler.getValue());
    }
    if (valuesReadHandler != null)
    {
      parameter.setSelections(valuesReadHandler.getSelections());
    }
  }

  /**
   * Returns the object for this element or null, if this element does
   * not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return parameter;
  }

  public Parameter getParameter()
  {
    return parameter;
  }
}
