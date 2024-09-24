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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

public interface ReportPreProcessorMetaData extends MetaData {
  public Class getPreProcessorType();

  public BeanInfo getBeanDescriptor() throws IntrospectionException;

  public String[] getPropertyNames();

  public ReportPreProcessorPropertyMetaData[] getPropertyDescriptions();

  public ReportPreProcessorPropertyMetaData getPropertyDescription( String name );

  public boolean isAutoProcessor();

  public boolean isExecuteInDesignMode();

  public ReportPreProcessor create() throws InstantiationException;

  public int getExecutionPriority();
}
