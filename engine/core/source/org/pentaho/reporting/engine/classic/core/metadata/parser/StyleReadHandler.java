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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** @noinspection HardCodedStringLiteral*/
public class StyleReadHandler extends AbstractXmlReadHandler
{
  private String bundleName;
  private String name;
  private String propertyEditor;
  private boolean preferred;
  private boolean mandatory;
  private boolean expert;
  private boolean hidden;
  private boolean deprecated;
  private boolean experimental;
  private int compatibilityLevel;

  public StyleReadHandler(final String bundleName)
  {
    this.bundleName = bundleName;
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

    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));
    mandatory = "true".equals(attrs.getValue(getUri(), "mandatory"));
    expert = "true".equals(attrs.getValue(getUri(), "expert"));
    hidden = "true".equals(attrs.getValue(getUri(), "hidden"));
    preferred = "true".equals(attrs.getValue(getUri(), "preferred"));
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated"));
    propertyEditor = attrs.getValue(getUri(), "propertyEditor");

    final String bundleFromAttributes = attrs.getValue(getUri(), "bundle-name");
    if (bundleFromAttributes != null)
    {
      bundleName = bundleFromAttributes;
    }
  }

  public String getPropertyEditor()
  {
    return propertyEditor;
  }

  public boolean isDeprecated()
  {
    return deprecated;
  }

  public String getName()
  {
    return name;
  }

  public boolean isPreferred()
  {
    return preferred;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public boolean isExpert()
  {
    return expert;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public boolean isExperimental()
  {
    return experimental;
  }

  public int getCompatibilityLevel()
  {
    return compatibilityLevel;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return this;
  }

  public String getBundleName()
  {
    return bundleName;
  }
}