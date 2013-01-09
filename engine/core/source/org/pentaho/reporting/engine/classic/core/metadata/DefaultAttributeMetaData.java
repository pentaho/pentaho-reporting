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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.beans.PropertyEditor;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultAttributeMetaData extends AbstractMetaData implements AttributeMetaData
{
  private String valueRole;
  private boolean bulk;
  private String namespace;
  private Class targetClass;
  private boolean mandatory;

  /**
   * Indicates if the value is computed. A computed value does not have to be retained since it can be re-computed.
   * NOTE: an Element that is computed may or may not be transient.
   */
  private boolean computed;

  /**
   * Indicates if the value is transient. These values will not be written when the Element is serialized.
   * NOTE: an Element that is computed may or may not be transient.
   */
  private boolean transientFlag;

  private String propertyEditorClass;
  private boolean designTimeValue;
  private AttributeCore attributeCore;

  public DefaultAttributeMetaData(final String namespace,
                                  final String name,
                                  final String bundleLocation,
                                  final String keyPrefix,
                                  final String propertyEditorClass,
                                  final Class targetClass,
                                  final boolean expert,
                                  final boolean preferred,
                                  final boolean hidden,
                                  final boolean deprecated,
                                  final boolean mandatory,
                                  final boolean computed,
                                  final boolean transientFlag,
                                  final String valueRole,
                                  final boolean bulk,
                                  final boolean designTimeValue,
                                  final AttributeCore attributeCore,
                                  final boolean experimental,
                                   final int compatibilityLevel)
  {
    super(name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, experimental, compatibilityLevel);
    if (namespace == null)
    {
      throw new NullPointerException();
    }
    if (attributeCore == null)
    {
      throw new NullPointerException();
    }
    if (targetClass == null)
    {
      throw new NullPointerException();
    }

    this.attributeCore = attributeCore;
    this.propertyEditorClass = propertyEditorClass;
    this.namespace = namespace;
    this.targetClass = targetClass;
    this.mandatory = mandatory;
    this.computed = computed;
    this.transientFlag = transientFlag;
    this.valueRole = valueRole;
    this.bulk = bulk;
    this.designTimeValue = designTimeValue;
  }

  /**
   * Can be one of "Value", "Resource", "Content", "Field", "Group", "Query", "Message", "Bundle-Key", "Bundle-Name",
   * "Name", "ElementName",
   *
   * @return
   */
  public String getValueRole()
  {
    return valueRole;
  }

  public boolean isDesignTimeValue()
  {
    return designTimeValue;
  }

  public boolean isBulk()
  {
    return bulk;
  }

  public boolean isComputed()
  {
    return computed;
  }

  public boolean isTransient()
  {
    return transientFlag;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public String getNameSpace()
  {
    return namespace;
  }

  public Class getTargetType()
  {
    return targetClass;
  }

  public PropertyEditor getEditor()
  {
    if (propertyEditorClass == null)
    {
      return null;
    }
    return (PropertyEditor) ObjectUtilities.loadAndInstantiate
        (propertyEditorClass, DefaultAttributeMetaData.class, PropertyEditor.class);
  }

  public String[] getReferencedFields(final ReportElement element, final Object attributeValue)
  {
    return attributeCore.getReferencedFields(this, element, attributeValue);
  }

  public String[] getReferencedGroups(final ReportElement element, final Object attributeValue)
  {
    return attributeCore.getReferencedGroups(this, element, attributeValue);
  }

  public ResourceReference[] getReferencedResources(final ReportElement element,
                                                    final ResourceManager resourceManager,
                                                    final Object attributeValue)
  {
    return attributeCore.getReferencedResources(this, element, resourceManager, attributeValue);
  }

  public String toString()
  {
    return "org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData{" +
        "valueRole='" + valueRole + '\'' +
        ", namespace='" + namespace + '\'' +
        ", name='" + getName() + '\'' +
        ", targetType=" + targetClass +
        ", mandatory=" + mandatory +
        ", computed=" + computed +
        ", transient=" + transientFlag +
        ", editor=" + getEditor() +
        '}';
  }

  public String[] getExtraCalculationFields()
  {
    return attributeCore.getExtraCalculationFields(this);
  }
}
