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

import org.pentaho.reporting.engine.classic.core.metadata.AttributeCore;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeCore;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AttributeReadHandler extends AbstractMetaDataReadHandler
{
  private String namespace;
  private boolean mandatory;
  private boolean computed;
  private boolean transientFlag;
  private Class valueType;
  private String valueRole;
  private boolean bulk;
  private String propertyEditor;
  private boolean designTimeValue;
  private AttributeCore attributeCore;

  public AttributeReadHandler(final String bundle)
  {
    super(bundle);
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    super.startParsing(attrs);
    namespace = attrs.getValue(getUri(), "namespace"); // NON-NLS
    if (namespace == null)
    {
      throw new ParseException("Attribute 'namespace' is undefined", getLocator());
    }

    mandatory = "true".equals(attrs.getValue(getUri(), "mandatory")); // NON-NLS
    computed = "true".equals(attrs.getValue(getUri(), "computed")); // NON-NLS
    transientFlag = "true".equals(attrs.getValue(getUri(), "transient")); // NON-NLS
    bulk = "true".equals(attrs.getValue(getUri(), "prefer-bulk")); // NON-NLS
    designTimeValue = "true".equals(attrs.getValue(getUri(), "design-time-value")); // NON-NLS

    final String valueTypeText = attrs.getValue(getUri(), "value-type"); // NON-NLS
    if (valueTypeText == null)
    {
      throw new ParseException("Attribute 'value-type' is undefined", getLocator());
    }
    try
    {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader(getClass());
      valueType = Class.forName(valueTypeText, false, classLoader);
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute 'value-type' is not valid", e, getLocator());
    }

    valueRole = attrs.getValue(getUri(), "value-role"); // NON-NLS
    if (valueRole == null)
    {
      valueRole = "Value"; // NON-NLS
    }

    propertyEditor = attrs.getValue(getUri(), "propertyEditor"); // NON-NLS

    final String metaDataCoreClass = attrs.getValue(getUri(), "impl"); // NON-NLS
    if (metaDataCoreClass != null)
    {
      attributeCore = ObjectUtilities.loadAndInstantiate
          (metaDataCoreClass, AttributeReadHandler.class, AttributeCore.class);
      if (attributeCore == null)
      {
        throw new ParseException("Attribute 'impl' references a invalid AttributeCore implementation.", getLocator());
      }
    }
    else
    {
      attributeCore = new DefaultAttributeCore();
    }
  }

  public AttributeCore getAttributeCore()
  {
    return attributeCore;
  }

  public String getPropertyEditor()
  {
    return propertyEditor;
  }

  public String getNamespace()
  {
    return namespace;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public boolean isComputed()
  {
    return computed;
  }

  public boolean isTransient()
  {
    return transientFlag;
  }

  public Class getValueType()
  {
    return valueType;
  }

  public boolean isBulk()
  {
    return bulk;
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
  public AttributeDefinition getObject() throws SAXException
  {
    return new AttributeDefinition(namespace, getName(), isPreferred(), mandatory,
        isExpert(), isHidden(), computed, transientFlag, valueType, valueRole, propertyEditor,
        isDeprecated(), bulk, designTimeValue, getBundle(), attributeCore, isExperimental(), getCompatibilityLevel());
  }

  public boolean isDesignTimeValue()
  {
    return designTimeValue;
  }
}
