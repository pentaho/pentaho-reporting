/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
