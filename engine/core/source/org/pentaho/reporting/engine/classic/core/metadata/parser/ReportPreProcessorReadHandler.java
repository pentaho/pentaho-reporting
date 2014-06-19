/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** @noinspection HardCodedStringLiteral*/
public class ReportPreProcessorReadHandler extends AbstractMetaDataReadHandler
{
  private Class expressionClass;

  private ArrayList<ReportPreProcessorPropertyReadHandler> attributeHandlers;
  private SharedBeanInfo beanInfo;
  private HashMap<String,ReportPreProcessorPropertyMetaData> properties;
  private boolean autoProcess;
  private boolean executeInDesignMode;
  private int executionPriority;

  public ReportPreProcessorReadHandler()
  {
    attributeHandlers = new ArrayList<ReportPreProcessorPropertyReadHandler>();
    properties = new HashMap<String,ReportPreProcessorPropertyMetaData>();
  }

  protected boolean isDerivedName()
  {
    return true;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);
    autoProcess = "true".equals(attrs.getValue(getUri(), "auto-process"));
    executionPriority = ParserUtil.parseInt(attrs.getValue(getUri(), "priority"), 0);
    executeInDesignMode = "true".equals(attrs.getValue(getUri(), "execute-in-design-mode"));

    final String valueTypeText = attrs.getValue(getUri(), "class");
    if (valueTypeText == null)
    {
      throw new ParseException("Attribute 'class' is undefined", getLocator());
    }
    try
    {
      final ClassLoader loader = ObjectUtilities.getClassLoader(ReportPreProcessorReadHandler.class);
      expressionClass = Class.forName(valueTypeText, false, loader);
      if (ReportPreProcessor.class.isAssignableFrom(expressionClass) == false)
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
      final ReportPreProcessorPropertyReadHandler readHandler =
          new ReportPreProcessorPropertyReadHandler(beanInfo, getBundle());
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
      final ReportPreProcessorPropertyReadHandler handler = attributeHandlers.get(i);
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
    return new DefaultReportPreProcessorMetaData(getBundle(),
        isExpert(), isPreferred(), isHidden(), isDeprecated(),
        expressionClass, properties, beanInfo, autoProcess, executeInDesignMode,
            isExperimental(), getCompatibilityLevel(), executionPriority);
  }
}
