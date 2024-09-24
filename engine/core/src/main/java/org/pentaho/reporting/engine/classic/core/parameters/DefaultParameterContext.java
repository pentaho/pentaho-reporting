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

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultParameterContext implements ParameterContext {
  private ReportEnvironment reportEnvironment;
  private CompoundDataRow parameterValues;
  private ResourceBundleFactory resourceBundleFactory;
  private Configuration configuration;
  private CachingDataFactory dataFactory;
  private ResourceKey contentBase;
  private ResourceManager resourceManager;
  private DocumentMetaData documentMetaData;
  private ReportEnvironmentDataRow envDataRow;

  public DefaultParameterContext( final MasterReport report ) throws ReportProcessingException {
    this( report, report.getParameterValues() );
  }

  public DefaultParameterContext( final MasterReport report, final ReportParameterValues parameterValues )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException( "Report parameter must not be null" );
    }
    if ( parameterValues == null ) {
      throw new NullPointerException( "ParameterValues parameter must not be null" );
    }
    this.configuration = report.getConfiguration();
    this.resourceBundleFactory =
        MasterReport.computeAndInitResourceBundleFactory( report.getResourceBundleFactory(), report
            .getReportEnvironment() );
    this.contentBase = report.getContentBase();
    this.resourceManager = report.getResourceManager();
    this.reportEnvironment = report.getReportEnvironment();
    final Object dataCacheEnabledRaw =
        report.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.DATA_CACHE );
    final boolean dataCacheEnabled = Boolean.FALSE.equals( dataCacheEnabledRaw ) == false;
    this.dataFactory = new CachingDataFactory( report.getDataFactory(), dataCacheEnabled );

    final DocumentBundle bundle = report.getBundle();
    if ( bundle != null ) {
      this.documentMetaData = bundle.getMetaData();
    }
    this.dataFactory.initialize( new DesignTimeDataFactoryContext( configuration, resourceManager, contentBase,
        resourceBundleFactory, dataFactory ) );

    final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow( reportEnvironment );
    this.parameterValues = new CompoundDataRow( envDataRow, parameterValues );

  }

  public DefaultParameterContext( final DataFactory dataFactory, final DataRow parameterValues,
      final Configuration configuration, final ResourceBundleFactory resourceBundleFactory,
      final ResourceManager resourceManager, final ResourceKey resourceKey, final ReportEnvironment reportEnvironment )
    throws ReportDataFactoryException {
    this.configuration = configuration;
    this.resourceBundleFactory = resourceBundleFactory;
    this.resourceManager = resourceManager;
    this.contentBase = resourceKey;
    this.reportEnvironment = reportEnvironment;
    this.dataFactory = new CachingDataFactory( dataFactory, false );
    this.dataFactory.initialize( new DesignTimeDataFactoryContext( configuration, resourceManager, resourceKey,
        resourceBundleFactory, dataFactory ) );

    this.envDataRow = new ReportEnvironmentDataRow( reportEnvironment );
    this.parameterValues = new CompoundDataRow( envDataRow, parameterValues );
  }

  public DocumentMetaData getDocumentMetaData() {
    return documentMetaData;
  }

  public ReportEnvironment getReportEnvironment() {
    return reportEnvironment;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public DataRow getParameterData() {
    return parameterValues;
  }

  public DataRow getParameterValues() {
    return parameterValues.getDataRow();
  }

  public void setParameterValues( final DataRow parameterValues ) {
    if ( parameterValues == null ) {
      throw new NullPointerException();
    }
    this.parameterValues = new CompoundDataRow( envDataRow, parameterValues );
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  public void close() throws ReportDataFactoryException {
    this.dataFactory.close();
  }

  public ResourceKey getContentBase() {
    return contentBase;
  }

  public PerformanceMonitorContext getPerformanceMonitorContext() {
    return ClassicEngineBoot.getInstance().getObjectFactory().get( PerformanceMonitorContext.class );
  }
}
