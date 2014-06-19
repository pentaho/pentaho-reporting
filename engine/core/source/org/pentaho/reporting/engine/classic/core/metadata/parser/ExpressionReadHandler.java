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

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** @noinspection HardCodedStringLiteral*/
public class ExpressionReadHandler extends AbstractXmlReadHandler
{
  private String bundleName;
  private Class expressionClass;
  private Class resultType;

  private boolean expert;
  private boolean hidden;
  private boolean preferred;
  private boolean deprecated;
  private ArrayList<ExpressionPropertyReadHandler> attributeHandlers;
  private SharedBeanInfo beanInfo;
  private HashMap<String,ExpressionPropertyMetaData> properties;
  private int layoutComputation;
  private boolean experimental;
  private int compatibilityLevel;

  public ExpressionReadHandler()
  {
    attributeHandlers = new ArrayList<ExpressionPropertyReadHandler>();
    properties = new HashMap<String,ExpressionPropertyMetaData>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    bundleName = attrs.getValue(getUri(), "bundle-name");
    expert = "true".equals(attrs.getValue(getUri(), "expert"));
    hidden = "true".equals(attrs.getValue(getUri(), "hidden"));
    preferred = "true".equals(attrs.getValue(getUri(), "preferred"));
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated"));
    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));

    final String layoutProcessorMode = attrs.getValue(getUri(), "layout-processor-mode");
    if ("global".equals(layoutProcessorMode))
    {
      layoutComputation = DefaultExpressionMetaData.GLOBAL_LAYOUT_PROCESSOR;
    }
    else if ("element".equals(layoutProcessorMode))
    {
      layoutComputation = DefaultExpressionMetaData.ELEMENT_LAYOUT_PROCESSOR;
    }
    else
    {
      layoutComputation = DefaultExpressionMetaData.NO_LAYOUT_PROCESSOR;
    }

    final String valueTypeText = attrs.getValue(getUri(), "class");
    if (valueTypeText == null)
    {
      throw new ParseException("Attribute 'class' is undefined", getLocator());
    }
    try
    {
      final ClassLoader loader = ObjectUtilities.getClassLoader(ExpressionReadHandler.class);
      expressionClass = Class.forName(valueTypeText, false, loader);
      if (Expression.class.isAssignableFrom(expressionClass) == false)
      {
        throw new ParseException("Attribute 'class' is not valid", getLocator());
      }
    }
    catch (ParseException pe)
    {
      throw pe;
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute 'class' is not valid", e, getLocator());
    }

    beanInfo = new SharedBeanInfo(expressionClass);

    final String resultType = attrs.getValue(getUri(), "result");
    if (resultType == null)
    {
      throw new ParseException("Attribute 'result' is undefined", getLocator());
    }
    try
    {
      final ClassLoader loader = ObjectUtilities.getClassLoader(ExpressionReadHandler.class);
      this.resultType = Class.forName(resultType, false, loader);
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute 'result' is not valid", e, getLocator());
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


    if ("property".equals(tagName))
    {
      final ExpressionPropertyReadHandler readHandler = new ExpressionPropertyReadHandler(beanInfo, bundleName);
      attributeHandlers.add(readHandler);
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
      final ExpressionPropertyReadHandler handler = attributeHandlers.get(i);
      final String attrName = handler.getName();
      properties.put(attrName, handler.getObject());
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
    return new DefaultExpressionMetaData(bundleName, expert, preferred, hidden, deprecated,
        expressionClass, resultType, properties, beanInfo, layoutComputation, experimental, compatibilityLevel);
  }
}
