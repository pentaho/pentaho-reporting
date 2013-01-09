package org.pentaho.reporting.engine.classic.extensions.drilldown.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.LinkCustomizer;
import org.pentaho.reporting.engine.classic.extensions.drilldown.PatternLinkCustomizer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DrillDownProfileReadHandler extends AbstractXmlReadHandler
{
  private ArrayList<PropertyReadHandler> attributes;
  private String bundleName;
  private Class linkCustomizerType;
  private String name;
  private String prefix;

  private boolean expert;
  private boolean hidden;
  private boolean preferred;
  private boolean deprecated;
  private String group;

  public DrillDownProfileReadHandler(final String group)
  {
    this.group = group;
    attributes = new ArrayList<PropertyReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    name = attrs.getValue(getUri(), "name"); // NON-NLS
    if (name == null)
    {
      throw new ParseException("Attribute 'name' is undefined.", getLocator()); // NON-NLS
    }
    bundleName = attrs.getValue(getUri(), "bundle-name"); // NON-NLS
    if (bundleName == null)
    {
      throw new ParseException("Attribute 'bundle-name' is undefined.", getLocator()); // NON-NLS
    }
    expert = "true".equals(attrs.getValue(getUri(), "expert")); // NON-NLS
    hidden = "true".equals(attrs.getValue(getUri(), "hidden")); // NON-NLS
    preferred = "true".equals(attrs.getValue(getUri(), "preferred")); // NON-NLS
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated")); // NON-NLS

    final String valueTypeText = attrs.getValue(getUri(), "class"); // NON-NLS
    if (valueTypeText != null)
    {
      try
      {
        final ClassLoader loader = ObjectUtilities.getClassLoader(DrillDownProfileReadHandler.class);
        linkCustomizerType = Class.forName(valueTypeText, false, loader);
        if (LinkCustomizer.class.isAssignableFrom(linkCustomizerType) == false)
        {
          //noinspection ThrowCaughtLocally
          throw new ParseException("Attribute 'class' is not valid", getLocator()); // NON-NLS
        }
      }
      catch (ParseException pe)
      {
        throw pe;
      }
      catch (Exception e)
      {
        throw new ParseException("Attribute 'class' is not valid", e, getLocator()); // NON-NLS
      }
    }
    else
    {
      linkCustomizerType = PatternLinkCustomizer.class;
    }
    prefix = "drilldown-profile."; // NON-NLS
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
    if ("attribute".equals(tagName)) // NON-NLS
    {
      final PropertyReadHandler propertyReadHandler = new PropertyReadHandler();
      attributes.add(propertyReadHandler);
      return propertyReadHandler;
    }
    return super.getHandlerForChild(uri, tagName, atts);
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    final HashMap<String, String> attrMap = new HashMap<String, String>();
    for (int i = 0; i < attributes.size(); i++)
    {
      final PropertyReadHandler readHandler = attributes.get(i);
      attrMap.put(readHandler.getName(), readHandler.getResult());
    }
    attrMap.put("group", group); // NON-NLS
    return new DrillDownProfile
        (name, bundleName, prefix, expert, preferred, hidden, deprecated, linkCustomizerType, attrMap, false, -1);
  }
}
