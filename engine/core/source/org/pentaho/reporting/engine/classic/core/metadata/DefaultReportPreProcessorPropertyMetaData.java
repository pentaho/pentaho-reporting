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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultReportPreProcessorPropertyMetaData extends AbstractMetaData
    implements ReportPreProcessorPropertyMetaData
{
  private boolean mandatory;
  private String propertyRole;
  private String propertyEditorClass;
  private boolean computed;
  private ReportPreProcessorPropertyCore reportPreProcessorCore;

  private transient PropertyDescriptor propertyDescriptor;
  private transient SharedBeanInfo beanInfo;

  public DefaultReportPreProcessorPropertyMetaData(final String name,
                                                   final String bundleLocation,
                                                   final boolean expert,
                                                   final boolean preferred,
                                                   final boolean hidden,
                                                   final boolean deprecated,
                                                   final boolean mandatory,
                                                   final boolean computed,
                                                   final String propertyRole,
                                                   final SharedBeanInfo beanInfo,
                                                   final String propertyEditorClass,
                                                   final ReportPreProcessorPropertyCore reportPreProcessorCore,
                                                   final boolean experimental,
                                                   final int compatibilityLevel)
  {
    super(name, bundleLocation, "property.", expert, preferred, hidden, deprecated, experimental, compatibilityLevel);
    ArgumentNullException.validate("propertyRole", propertyRole);
    ArgumentNullException.validate("beanInfo", beanInfo);
    ArgumentNullException.validate("reportPreProcessorCore", reportPreProcessorCore);

    this.beanInfo = beanInfo;
    this.reportPreProcessorCore = reportPreProcessorCore;
    this.computed = computed;
    this.propertyEditorClass = propertyEditorClass;
    this.mandatory = mandatory;
    this.propertyRole = propertyRole;
  }

  public boolean isComputed()
  {
    return computed;
  }

  public Class getPropertyType()
  {
    if (propertyDescriptor == null)
    {
      propertyDescriptor = beanInfo.getPropertyDescriptor(getName());
    }
    return propertyDescriptor.getPropertyType();
  }

  public String getPropertyRole()
  {
    return propertyRole;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public String[] getReferencedFields(final Expression element, final Object attributeValue)
  {
    return reportPreProcessorCore.getReferencedFields(this, element, attributeValue);
  }

  public String[] getReferencedGroups(final Expression element, final Object attributeValue)
  {
    return reportPreProcessorCore.getReferencedGroups(this, element, attributeValue);
  }

  public String[] getReferencedElements(final Expression expression, final Object attributeValue)
  {
    return reportPreProcessorCore.getReferencedElements(this, expression, attributeValue);
  }

  public ResourceReference[] getReferencedResources(final Expression expression,
                                                    final Object attributeValue,
                                                    final Element reportElement,
                                                    final ResourceManager resourceManager)
  {
    return reportPreProcessorCore.getReferencedResources(this, expression, attributeValue, reportElement, resourceManager);
  }

  public PropertyDescriptor getBeanDescriptor()
  {
    if (propertyDescriptor == null)
    {
      propertyDescriptor = beanInfo.getPropertyDescriptor(getName());
    }
    return propertyDescriptor;
  }

  public PropertyEditor getEditor()
  {
    if (propertyEditorClass == null)
    {
      return null;
    }
    return ObjectUtilities.loadAndInstantiate
        (propertyEditorClass, DefaultAttributeMetaData.class, PropertyEditor.class);
  }

  public String[] getExtraCalculationFields()
  {
    return reportPreProcessorCore.getExtraCalculationFields(this);
  }

  private void writeObject(ObjectOutputStream out)
     throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(beanInfo.getBeanClass());
  }

  private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    final Class c = (Class) in.readObject();
    beanInfo = new SharedBeanInfo(c);
  }
}
