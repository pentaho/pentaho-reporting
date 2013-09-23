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

import org.pentaho.reporting.engine.classic.core.metadata.AttributeCore;

public class AttributeDefinition
{
  private String bundleName;
  private String namespace;
  private String name;
  private String namespacePrefix;
  private boolean preferred;
  private boolean mandatory;
  private boolean expert;
  private boolean hidden;
  private boolean computed;
  private boolean transientFlag;
  private Class<?> valueType;
  private String valueRole;
  private boolean deprecated;
  private boolean bulk;
  private String propertyEditor;
  private boolean designTimeValue;
  private AttributeCore attributeCore;
  private boolean experimental;
  private int compatibilityLevel;

  public AttributeDefinition(final String namespace,
                             final String namespacePrefix,
                             final String name,
                             final boolean preferred,
                             final boolean mandatory,
                             final boolean expert,
                             final boolean hidden,
                             final boolean computed,
                             final boolean transientFlag,
                             final Class<?> valueType,
                             final String valueRole,
                             final String propertyEditor,
                             final boolean deprecated,
                             final boolean bulk,
                             final boolean designTimeValue,
                             final String bundleName,
                             final AttributeCore attributeCore,
                             final boolean experimental,
                             final int compatibilityLevel)
  {
    if (namespace == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }
    if (valueType == null)
    {
      throw new NullPointerException();
    }
    if (valueRole == null)
    {
      throw new NullPointerException();
    }
    if (attributeCore == null)
    {
      throw new NullPointerException();
    }

    this.namespacePrefix = namespacePrefix;
    this.attributeCore = attributeCore;
    this.propertyEditor = propertyEditor;
    this.namespace = namespace;
    this.name = name;
    this.preferred = preferred;
    this.mandatory = mandatory;
    this.expert = expert;
    this.hidden = hidden;
    this.computed = computed;
    this.transientFlag = transientFlag;
    this.valueType = valueType;
    this.valueRole = valueRole;
    this.deprecated = deprecated;
    this.bulk = bulk;
    this.designTimeValue = designTimeValue;
    this.bundleName = bundleName;
    this.experimental = experimental;
    this.compatibilityLevel = compatibilityLevel;
  }

  public AttributeCore getAttributeCore()
  {
    return attributeCore;
  }

  public String getPropertyEditor()
  {
    return propertyEditor;
  }

  public boolean isBulk()
  {
    return bulk;
  }

  public String getNamespace()
  {
    return namespace;
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

  public boolean isComputed()
  {
    return computed;
  }

  public boolean isTransient()
  {
    return transientFlag;
  }

  public String getNamespacePrefix()
  {
    return namespacePrefix;
  }

  public Class<?> getValueType()
  {
    return valueType;
  }

  public String getValueRole()
  {
    return valueRole;
  }

  public boolean isDeprecated()
  {
    return deprecated;
  }

  public boolean isDesignTimeValue()
  {
    return designTimeValue;
  }

  public String getBundleName()
  {
    return bundleName;
  }

  public boolean isExperimental()
  {
    return experimental;
  }

  public int getCompatibilityLevel()
  {
    return compatibilityLevel;
  }
}
