/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultStyleKeyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** @noinspection HardCodedStringLiteral*/
public class ElementReadHandler extends AbstractXmlReadHandler
{
  private static final Log logger = LogFactory.getLog(ElementReadHandler.class);

  private String name;
  private boolean expert;
  private boolean hidden;
  private boolean preferred;
  private boolean deprecated;
  private ElementMetaData.TypeClassification reportElementType;
  private AttributeMap<AttributeMetaData> attributes;
  private HashMap<StyleKey,StyleMetaData> styles;
  private String bundleName;
  private String prefix;
  private Class elementType;
  private Class contentType;
  private String namespace;

  private ArrayList<StyleReadHandler> styleHandlers;
  private ArrayList<AttributeReadHandler> attributeHandlers;
  private GlobalMetaDefinition globalMetaDefinition;
  private boolean experimental;
  private int compatibilityLevel;

  public ElementReadHandler(final GlobalMetaDefinition globalMetaDefinition)
  {
    this.globalMetaDefinition = globalMetaDefinition;
    this.attributes = new AttributeMap<AttributeMetaData>();
    this.styles = new HashMap<StyleKey,StyleMetaData>();

    this.attributeHandlers = new ArrayList<AttributeReadHandler>();
    this.styleHandlers = new ArrayList<StyleReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    name = attrs.getValue(getUri(), "name");
    if (name == null)
    {
      throw new ParseException("Attribute 'name' is undefined", getLocator());
    }

    namespace = attrs.getValue(getUri(), "namespace"); // NON-NLS
    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));
    expert = "true".equals(attrs.getValue(getUri(), "expert"));
    hidden = "true".equals(attrs.getValue(getUri(), "hidden"));
    preferred = "true".equals(attrs.getValue(getUri(), "preferred"));
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated"));
    final boolean container = "true".equals(attrs.getValue(getUri(), "container"));
    if (container == false)
    {
      reportElementType = ElementMetaData.TypeClassification.DATA;
    }
    else
    {
      reportElementType = ElementMetaData.TypeClassification.SECTION;
    }

    final String eType = attrs.getValue(getUri(), "type-classification");
    if ("section".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.SECTION;
    }
    else if ("data".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.DATA;
    }
    else if ("control".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.CONTROL;
    }
    else if ("footer".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.FOOTER;
    }
    else if ("group-footer".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.RELATIONAL_FOOTER;
    }
    else if ("header".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.HEADER;
    }
    else if ("group-header".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.RELATIONAL_HEADER;
    }
    else if ("subreport".equals(eType))
    {
      reportElementType = ElementMetaData.TypeClassification.SUBREPORT;
    }

    bundleName = attrs.getValue(getUri(), "bundle-name");
    prefix = "element." + name;

    final String elementTypeText = attrs.getValue(getUri(), "implementation");
    if (elementTypeText == null)
    {
      throw new ParseException("Attribute 'implementation' is undefined", getLocator());
    }
    try
    {
      final ClassLoader loader = ObjectUtilities.getClassLoader(ElementReadHandler.class);
      elementType = Class.forName(elementTypeText, false, loader);
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute 'implementation' is not valid", e, getLocator());
    }

    if (ElementType.class.isAssignableFrom(elementType) == false)
    {
      throw new ParseException("Attribute 'implementation' is not valid", getLocator());
    }


    final String contentType = attrs.getValue(getUri(), "content-type");
    if (contentType == null)
    {
      this.contentType = Object.class;
    }
    else
    {
      try
      {
        final ClassLoader loader = ObjectUtilities.getClassLoader(ElementReadHandler.class);
        this.contentType = Class.forName(contentType, false, loader);
      }
      catch (Exception e)
      {
        throw new ParseException("Attribute 'content-type' is not valid", e, getLocator());
      }
    }
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


    if ("attribute-group-ref".equals(tagName))
    {
      return new AttributeGroupRefReadHandler(attributes, globalMetaDefinition, bundleName);
    }
    else if ("style-group-ref".equals(tagName))
    {
      return new StyleGroupRefReadHandler(styles, globalMetaDefinition, bundleName);
    }
    else if ("attribute".equals(tagName))
    {
      final AttributeReadHandler readHandler = new AttributeReadHandler(null);
      attributeHandlers.add(readHandler);
      return readHandler;
    }
    else if ("style".equals(tagName))
    {
      final StyleReadHandler readHandler = new StyleReadHandler(bundleName);
      styleHandlers.add(readHandler);
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
    for (int i = 0; i < attributeHandlers.size(); i++)
    {
      final AttributeReadHandler handler = attributeHandlers.get(i);
      final String namespace = handler.getNamespace();
      final String attrName = handler.getName();
      final String namespacePrefix = ElementTypeRegistry.getInstance().getNamespacePrefix(namespace);
      if (namespacePrefix == null)
      {
        ElementReadHandler.logger.warn("Invalid namespace-prefix, skipping attribute " + namespace + ':' + attrName);
        continue;
      }

      final String prefix;
      final String bundleName;
      if (handler.getBundle() != null)
      {
        bundleName = handler.getBundle();
        prefix = "attribute." + namespacePrefix + '.';
      }
      else
      {
        bundleName = this.bundleName;
        prefix = this.prefix + ".attribute." + namespacePrefix + '.';
      }
      final DefaultAttributeMetaData metaData = new DefaultAttributeMetaData
          (namespace, attrName, bundleName, prefix,
              handler.getPropertyEditor(), handler.getValueType(), handler.isExpert(),
              handler.isPreferred(), handler.isHidden(), handler.isDeprecated(),
              handler.isMandatory(), handler.isComputed(), handler.isTransient(), handler.getValueRole(),
              handler.isBulk(), handler.isDesignTimeValue(), handler.getAttributeCore(),
              handler.isExperimental(), handler.getCompatibilityLevel());
      attributes.setAttribute(namespace, attrName, metaData);
    }

    for (int i = 0; i < styleHandlers.size(); i++)
    {
      final StyleReadHandler handler = styleHandlers.get(i);
      final String keyName = handler.getName();
      final StyleKey key = StyleKey.getStyleKey(keyName);
      final DefaultStyleKeyMetaData metaData = new DefaultStyleKeyMetaData
          (key, handler.getPropertyEditor(), handler.getBundleName(), "style.",
              handler.isExpert(), handler.isPreferred(), handler.isHidden(), handler.isDeprecated(),
              handler.isExperimental(), handler.getCompatibilityLevel());
      styles.put(key, metaData);
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return new DefaultElementMetaData
        (name, bundleName, "element.", namespace, expert, preferred, hidden, deprecated, reportElementType,
            attributes, styles, elementType, contentType, experimental, compatibilityLevel);
  }
}
