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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ReportPreProcessorMetaDataBuilder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Map;

public class DefaultReportPreProcessorMetaData extends AbstractMetaData implements ReportPreProcessorMetaData {
  private Class<? extends ReportPreProcessor> expressionType;
  private Map<String, ReportPreProcessorPropertyMetaData> properties;
  private transient SharedBeanInfo beanInfo;
  private boolean autoProcessor;
  private boolean executeInDesignMode;
  private int executionPriority;

  @Deprecated
  public DefaultReportPreProcessorMetaData( final String bundleLocation, final boolean expert, final boolean preferred,
      final boolean hidden, final boolean deprecated, final Class<? extends ReportPreProcessor> expressionType,
      final HashMap<String, ReportPreProcessorPropertyMetaData> attributes, final SharedBeanInfo beanInfo,
      final boolean autoProcessor, final boolean executeInDesignMode, final MaturityLevel maturityLevel,
      final int compatibilityLevel, final int executionPriority ) {
    super( expressionType.getName(), bundleLocation, "", expert, preferred, hidden, deprecated, maturityLevel,
        compatibilityLevel );
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( beanInfo == null ) {
      throw new NullPointerException();
    }

    this.executionPriority = executionPriority;
    this.executeInDesignMode = executeInDesignMode;
    this.autoProcessor = autoProcessor;
    this.expressionType = expressionType;
    this.properties = (Map<String, ReportPreProcessorPropertyMetaData>) attributes.clone();
    this.beanInfo = beanInfo;
  }

  public DefaultReportPreProcessorMetaData( final ReportPreProcessorMetaDataBuilder builder ) {
    super( builder );
    this.executionPriority = builder.getPriority();
    this.executeInDesignMode = builder.isDesignMode();
    this.autoProcessor = builder.isAutoProcess();
    this.expressionType = builder.getImpl();
    this.properties = builder.getProperties();
  }

  public boolean isExecuteInDesignMode() {
    return executeInDesignMode;
  }

  protected String computePrefix( final String keyPrefix, final String name ) {
    return "";
  }

  public Class getPreProcessorType() {
    return expressionType;
  }

  public ReportPreProcessorPropertyMetaData getPropertyDescription( final String name ) {
    return properties.get( name );
  }

  public String[] getPropertyNames() {
    return properties.keySet().toArray( new String[properties.size()] );
  }

  public ReportPreProcessorPropertyMetaData[] getPropertyDescriptions() {
    return properties.values().toArray( new ReportPreProcessorPropertyMetaData[properties.size()] );
  }

  public BeanInfo getBeanDescriptor() throws IntrospectionException {
    if ( beanInfo == null ) {
      beanInfo = new SharedBeanInfo( expressionType );
    }
    return beanInfo.getBeanInfo();
  }

  public boolean isAutoProcessor() {
    return autoProcessor;
  }

  public ReportPreProcessor create() throws InstantiationException {
    try {
      return expressionType.newInstance();
    } catch ( IllegalAccessException e ) {
      throw new InstantiationException( "Unable to instantiate " + expressionType + ": IllegalAccessException caught" );
    }
  }

  public int getExecutionPriority() {
    return executionPriority;
  }
}
