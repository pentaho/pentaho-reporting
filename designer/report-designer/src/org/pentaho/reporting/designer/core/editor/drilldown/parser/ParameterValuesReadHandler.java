package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import java.util.ArrayList;

import org.pentaho.reporting.designer.core.editor.drilldown.model.ParameterSelection;
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
public class ParameterValuesReadHandler extends AbstractXmlReadHandler
{
  private ParameterSelection[] selections;
  private ArrayList selectionHandlers;
  private String parameterType;

  public ParameterValuesReadHandler(final String parameterType)
  {
    this.parameterType = parameterType;
    selectionHandlers = new ArrayList();
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
    if (isSameNamespace(uri))
    {
      if ("value".equals(tagName))
      {
        final ParameterValueReadHandler readHandler = new ParameterValueReadHandler(parameterType);
        selectionHandlers.add(readHandler);
        return readHandler;
      }
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
    selections = new ParameterSelection[selectionHandlers.size()];
    for (int i = 0; i < selectionHandlers.size(); i++)
    {
      final ParameterValueReadHandler handler = (ParameterValueReadHandler) selectionHandlers.get(i);
      selections[i] = handler.getSelection();
    }
    super.doneParsing();
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
    return selections;
  }

  public ParameterSelection[] getSelections()
  {
    return selections;
  }
}