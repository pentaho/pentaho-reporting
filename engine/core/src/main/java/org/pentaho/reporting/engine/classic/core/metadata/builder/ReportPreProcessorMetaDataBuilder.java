/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
