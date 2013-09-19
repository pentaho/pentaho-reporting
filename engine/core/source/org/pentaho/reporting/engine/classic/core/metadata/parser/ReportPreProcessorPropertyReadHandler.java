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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @noinspection HardCodedStringLiteral
 */
public class ReportPreProcessorPropertyReadHandler extends AbstractXmlReadHandler
{
  private boolean validatePropertiesOnBoot;
  private String name;
  private boolean preferred;
  private boolean mandatory;
  private boolean expert;
  private boolean hidden;
  private String valueRole;
  private boolean deprecated;
  private SharedBeanInfo expression;
  private String bundleLocation;
  private String propertyEditorClass;
  private boolean computed;
  private ReportPreProcessorPropertyCore reportPreProcessorPropertyCore;
  private boolean experimental;
  private int compatibilityLevel;

  public ReportPreProcessorPropertyReadHandler(final SharedBeanInfo expression,
                                               final String bundleLocation)
  {
    this.validatePropertiesOnBoot = "true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.metadata.StrictValidation"));
    this.expression = expression;
    this.bundleLocation = bundleLocation;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    name = attrs.getValue(getUri(), "name");
    if (name == null)
    {
      throw new ParseException("Attribute 'name' is undefined", getLocator());
    }

    mandatory = "true".equals(attrs.getValue(getUri(), "mandatory"));
    expert = "true".equals(attrs.getValue(getUri(), "expert"));
    hidden = "true".equals(attrs.getValue(getUri(), "hidden"));
    preferred = "true".equals(attrs.getValue(getUri(), "preferred"));
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated"));
    computed = "true".equals(attrs.getValue(getUri(), "computed"));
    experimental = "true".equals(attrs.getValue(getUri(), "experimental")); // NON-NLS
    compatibilityLevel = ReportParserUtil.parseVersion(attrs.getValue(getUri(), "compatibility-level"));

    valueRole = attrs.getValue(getUri(), "value-role");
    if (valueRole == null)
    {
      valueRole = "Value";
    }

    propertyEditorClass = attrs.getValue(getUri(), "propertyEditor");

    if (validatePropertiesOnBoot)
    {
      if (expression.getPropertyDescriptor(name) == null)
      {
        throw new ParseException("Attribute 'name' with value '" + name + "' does not reference a valid property. ["
            + expression + "]", getLocator());
      }
    }


    final String metaDataCoreClass = attrs.getValue(getUri(), "impl"); // NON-NLS
    if (metaDataCoreClass != null)
    {
      reportPreProcessorPropertyCore = ObjectUtilities.loadAndInstantiate
          (metaDataCoreClass, ReportPreProcessorPropertyReadHandler.class, ReportPreProcessorPropertyCore.class);
      if (reportPreProcessorPropertyCore == null)
      {
        throw new ParseException("Attribute 'impl' references a invalid ReportPreProcessorPropertyCore implementation.", getLocator());
      }
    }
    else
    {
      reportPreProcessorPropertyCore = new DefaultReportPreProcessorPropertyCore();
    }
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

  public boolean isDeprecated()
  {
    return deprecated;
  }

  public boolean isHidden()
  {
    return hidden;
  }

  public String getValueRole()
  {
    return valueRole;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public ReportPreProcessorPropertyMetaData getObject() throws SAXException
  {
    return new DefaultReportPreProcessorPropertyMetaData
        (name, bundleLocation, expert, preferred, hidden, deprecated, mandatory,
            computed, valueRole, expression, propertyEditorClass, reportPreProcessorPropertyCore,
            experimental, compatibilityLevel);
  }
}
