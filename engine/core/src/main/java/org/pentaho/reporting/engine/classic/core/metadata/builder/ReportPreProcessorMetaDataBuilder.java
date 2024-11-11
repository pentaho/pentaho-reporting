/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.metadata.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyMetaData;

public class ReportPreProcessorMetaDataBuilder extends MetaDataBuilder<ReportPreProcessorMetaDataBuilder> {
  private Class<? extends ReportPreProcessor> impl;
  private LinkedHashMap<String, ReportPreProcessorPropertyMetaData> properties;
  private boolean autoProcess;
  private boolean designMode;
  private int priority;

  public ReportPreProcessorMetaDataBuilder() {
    properties = new LinkedHashMap<String, ReportPreProcessorPropertyMetaData>();
  }

  public ReportPreProcessorMetaDataBuilder impl( final Class<? extends ReportPreProcessor> impl ) {
    this.impl = impl;
    return self();
  }

  public String getName() {
    if ( impl == null ) {
      return null;
    }
    return impl.getName();
  }

  public ReportPreProcessorMetaDataBuilder
    properties( final Map<String, ReportPreProcessorPropertyMetaData> properties ) {
    this.properties.putAll( properties );
    return self();
  }

  public ReportPreProcessorMetaDataBuilder property( final ReportPreProcessorPropertyMetaData p ) {
    this.properties.put( p.getName(), p );
    return self();
  }

  public ReportPreProcessorMetaDataBuilder autoProcess( final boolean autoProcess ) {
    this.autoProcess = autoProcess;
    return self();
  }

  public ReportPreProcessorMetaDataBuilder designMode( final boolean executeInDesignMode ) {
    this.designMode = executeInDesignMode;
    return self();
  }

  public ReportPreProcessorMetaDataBuilder priority( final int executionPriority ) {
    this.priority = executionPriority;
    return self();
  }

  public Class<? extends ReportPreProcessor> getImpl() {
    return impl;
  }

  public Map<String, ReportPreProcessorPropertyMetaData> getProperties() {
    return (Map<String, ReportPreProcessorPropertyMetaData>) properties.clone();
  }

  public boolean isAutoProcess() {
    return autoProcess;
  }

  public boolean isDesignMode() {
    return designMode;
  }

  public int getPriority() {
    return priority;
  }

  protected ReportPreProcessorMetaDataBuilder self() {
    return this;
  }
}
