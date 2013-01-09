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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportProcessTaskMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskMetaData;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @noinspection HardCodedStringLiteral
 */
public class ReportProcessTaskReadHandler extends AbstractMetaDataReadHandler
{
  private String bundleName;
  private Class expressionClass;
  private String[] aliases;
  private String configurationPrefix;
  private ArrayList<StringReadHandler> aliasReadHandlers;

  public ReportProcessTaskReadHandler()
  {
    aliasReadHandlers = new ArrayList<StringReadHandler>();
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
    bundleName = attrs.getValue(getUri(), "bundle-name");
    configurationPrefix = attrs.getValue(getUri(), "configuration-prefix");

    final String valueTypeText = attrs.getValue(getUri(), "class");
    if (valueTypeText == null)
    {
      throw new ParseException("Attribute 'class' is undefined", getLocator());
    }
    try
    {
      final ClassLoader loader = ObjectUtilities.getClassLoader(ExpressionReadHandler.class);
      expressionClass = Class.forName(valueTypeText, false, loader);
      if (ReportProcessTask.class.isAssignableFrom(expressionClass) == false)
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
  }

  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri) == false)
    {
      return null;
    }
    if ("alias".equals(tagName))
    {
      final StringReadHandler rh = new StringReadHandler();
      aliasReadHandlers.add(rh);
      return rh;
    }
    return null;
  }

  protected void doneParsing() throws SAXException
  {
    aliases = new String[aliasReadHandlers.size()];
    for (int i = 0; i < aliasReadHandlers.size(); i++)
    {
      final StringReadHandler readHandler = aliasReadHandlers.get(i);
      aliases[i] = readHandler.getResult();
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public ReportProcessTaskMetaData getObject() throws SAXException
  {
    return new DefaultReportProcessTaskMetaData(getName(), bundleName,
        isExpert(), isPreferred(), isHidden(), isDeprecated(), isExperimental(),
        getCompatibilityLevel(), expressionClass, configurationPrefix, aliases);
  }
}
