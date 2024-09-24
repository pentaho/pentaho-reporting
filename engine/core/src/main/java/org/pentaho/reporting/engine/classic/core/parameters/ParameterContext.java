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

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The parameter context is provided by the reporting engine to connect the parameter system with the data-sources and
 * user-defined parameters..
 *
 * @author Thomas Morgner
 */
public interface ParameterContext {
  /**
   * the document metadata of the report. Can be null, if the report does not have a bundle associated or if this
   * context is not part of a report-processing.
   *
   * @return
   */
  public DocumentMetaData getDocumentMetaData();

  public ReportEnvironment getReportEnvironment();

  public DataRow getParameterData();

  public DataFactory getDataFactory();

  public ResourceBundleFactory getResourceBundleFactory();

  public ResourceKey getContentBase();

  public ResourceManager getResourceManager();

  public Configuration getConfiguration();

  public PerformanceMonitorContext getPerformanceMonitorContext();

  public void close() throws ReportDataFactoryException;
}
