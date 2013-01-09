package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import java.util.ArrayList;
import java.util.Arrays;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DrillDownProfilesReadHandler extends AbstractXmlReadHandler
{
  private ArrayList<DrillDownGroupReadHandler> elements;
  private DrillDownProfileCollection typeCollection;

  public DrillDownProfilesReadHandler()
  {
    elements = new ArrayList<DrillDownGroupReadHandler>();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (getUri().equals(uri) == false)
    {
      return null;
    }
    if ("group".equals(tagName))
    {
      final DrillDownGroupReadHandler readHandler = new DrillDownGroupReadHandler();
      elements.add(readHandler);
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    final ArrayList<DrillDownProfile> results = new ArrayList<DrillDownProfile>();
    for (int i = 0; i < elements.size(); i++)
    {
      final DrillDownGroupReadHandler handler = elements.get(i);
      final DrillDownProfile[] profiles = (DrillDownProfile[]) handler.getObject();
      results.addAll(Arrays.asList(profiles));
    }

    typeCollection = new DrillDownProfileCollection(results.toArray(new DrillDownProfile[results.size()]));
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return typeCollection;
  }
}
