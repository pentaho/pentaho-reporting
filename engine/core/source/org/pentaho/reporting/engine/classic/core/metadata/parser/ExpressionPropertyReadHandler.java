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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.SharedBeanInfo;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ExpressionPropertyReadHandler extends AbstractXmlReadHandler
{
  private String name;
  private boolean preferred;
  private boolean mandatory;
  private boolean expert;
  private boolean hidden;
  private String valueRole;
  private boolean deprecated;
  private SharedBeanInfo beanInfo;
  private String bundleLocation;
  private String propertyEditorClass;
  private boolean computed;
  private ExpressionPropertyCore expressionPropertyCore;
  private boolean validatePropertiesOnBoot;
  private boolean experimental;
  private int compatibilityLevel;

  public ExpressionPropertyReadHandler(final SharedBeanInfo beanInfo,
                                       final String bundleLocation)
  {
    this.validatePropertiesOnBoot = "true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.metadata.StrictValidation"));
    this.beanInfo = beanInfo;
    this.bundleLocation = bundleLocation;
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
    mandatory = "true".equals(attrs.getValue(getUri(), "mandatory")); // NON-NLS
    expert = "true".equals(attrs.getValue(getUri(), "expert")); // NON-NLS
    hidden = "true".equals(attrs.getValue(getUri(), "hidden")); // NON-NLS
    preferred = "true".equals(attrs.getValue(getUri(), "preferred")); // NON-NLS
    deprecated = "true".equals(attrs.getValue(getUri(), "deprecated")); // NON-NLS
    computed = "true".equals(attrs.getValue(getUri(), "computed")); // NON-NLS

    valueRole = attrs.getValue(getUri(), "value-role"); // NON-NLS
    if (valueRole == null)
    {
      valueRole = "Value"; // NON-NLS
    }
    
    propertyEditorClass = attrs.getValue(getUri(), "propertyEditor"); // NON-NLS

    if (validatePropertiesOnBoot)
    {
      if (beanInfo.getPropertyDescriptor(name) == null)
      {
        throw new ParseException("Attribute 'name' with value '" + name + "' does not reference a valid property. ["
            + beanInfo.getBeanClass() + "]", getLocator());
      }
    }

    final String metaDataCoreClass = attrs.getValue(getUri(), "impl"); // NON-NLS
    if (metaDataCoreClass != null)
    {
      expressionPropertyCore = ObjectUtilities.loadAndInstantiate
          (metaDataCoreClass, ExpressionPropertyReadHandler.class, ExpressionPropertyCore.class);
      if (expressionPropertyCore == null)
      {
        throw new ParseException("Attribute 'impl' references a invalid ExpressionPropertyCore implementation.", getLocator());
      }
    }
    else
    {
      expressionPropertyCore = new DefaultExpressionPropertyCore();
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
   * @throws SAXException if an parser error occured.
   */
  public ExpressionPropertyMetaData getObject() throws SAXException
  {
    return new DefaultExpressionPropertyMetaData
        (name, bundleLocation, expert, preferred, hidden, deprecated, mandatory, computed, valueRole,
            beanInfo, propertyEditorClass, expressionPropertyCore, experimental, compatibilityLevel);
  }
}
