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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;

public class DefaultReportPreProcessorMetaData extends AbstractMetaData implements ReportPreProcessorMetaData
{
  private Class expressionType;
  private HashMap<String,ReportPreProcessorPropertyMetaData> properties;
  private transient SharedBeanInfo beanInfo;
  private boolean autoProcessor;
  private boolean executeInDesignMode;
  private int executionPriority;

  public DefaultReportPreProcessorMetaData(final String bundleLocation,
                                           final boolean expert,
                                           final boolean preferred,
                                           final boolean hidden,
                                           final boolean deprecated,
                                           final Class expressionType,
                                           final HashMap<String,ReportPreProcessorPropertyMetaData> attributes,
                                           final SharedBeanInfo beanInfo,
                                           final boolean autoProcessor,
                                           final boolean executeInDesignMode,
                                           final boolean experimental,
                                           final int compatibilityLevel,
                                           final int executionPriority)
  {
    super(expressionType.getName(), bundleLocation, "", expert,
        preferred, hidden, deprecated, experimental, compatibilityLevel);
    if (attributes == null)
    {
      throw new NullPointerException();
    }
    if (beanInfo == null)
    {
      throw new NullPointerException();
    }

    this.executionPriority = executionPriority;
    this.executeInDesignMode = executeInDesignMode;
    this.autoProcessor = autoProcessor;
    this.expressionType = expressionType;
    this.properties = (HashMap<String,ReportPreProcessorPropertyMetaData>) attributes.clone();
    this.beanInfo = beanInfo;
  }

  public boolean isExecuteInDesignMode()
  {
    return executeInDesignMode;
  }

  protected String computePrefix(final String keyPrefix, final String name)
  {
    return "";
  }

  public Class getPreProcessorType()
  {
    return expressionType;
  }

  public ReportPreProcessorPropertyMetaData getPropertyDescription(final String name)
  {
    return properties.get(name);
  }

  public String[] getPropertyNames()
  {
    return properties.keySet().toArray
        (new String[properties.size()]);
  }

  public ReportPreProcessorPropertyMetaData[] getPropertyDescriptions()
  {
    return properties.values().toArray
        (new ReportPreProcessorPropertyMetaData[properties.size()]);
  }

  public BeanInfo getBeanDescriptor() throws IntrospectionException
  {
    return beanInfo.getBeanInfo();
  }

  public boolean isAutoProcessor()
  {
    return autoProcessor;
  }

  public ReportPreProcessor create() throws InstantiationException
  {
    try
    {
      return (ReportPreProcessor) expressionType.newInstance();
    }
    catch (IllegalAccessException e)
    {
      throw new InstantiationException("Unable to instantiate " + expressionType + ": IllegalAccessException caught");
    }
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

  public int getExecutionPriority()
  {
    return executionPriority;
  }
}
