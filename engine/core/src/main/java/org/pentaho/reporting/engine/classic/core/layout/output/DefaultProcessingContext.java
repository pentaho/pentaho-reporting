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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.CachingReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.DefaultResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentMetaData;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContextFactory;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;

public class DefaultProcessingContext implements ProcessingContext {
  private FormulaContext formulaContext;
  private boolean prepareRun;
  private int processingLevel;
  private OutputProcessorMetaData outputProcessorMetaData;
  private ResourceBundleFactory resourceBundleFactory;
  private Configuration configuration;
  private int progressLevelCount;
  private int progressLevel;
  private ResourceManager resourceManager;
  private ResourceKey contentBase;
  private DocumentMetaData metaData;
  private ReportEnvironment reportEnvironment;
  private long startTime;
  private int compatibilityLevel;

  /**
   * This constructor exists for test-case use only. If you use this to process a real report, most of the settings of
   * the report will be ignored and your export will not work as expected.
   */
  public DefaultProcessingContext() {
    outputProcessorMetaData = new GenericOutputProcessorMetaData();
    resourceBundleFactory = new DefaultResourceBundleFactory();
    configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    resourceManager = new ResourceManager();
    reportEnvironment = new CachingReportEnvironment( new DefaultReportEnvironment( configuration ) );
    try {
      this.contentBase = resourceManager.createKey( new File( "." ) );
    } catch ( ResourceKeyCreationException rkce ) {
      this.contentBase = null;
    }
    formulaContext =
        DefaultFormulaContextFactory.INSTANCE.create( resourceBundleFactory.getLocale(), resourceBundleFactory
            .getTimeZone() );
    metaData = new MemoryDocumentMetaData();
    compatibilityLevel = -1;
  }

  public DefaultProcessingContext( final MasterReport report ) throws ReportProcessingException {
    this( report, new GenericOutputProcessorMetaData() );
  }

  public DefaultProcessingContext( final MasterReport report, final OutputProcessorMetaData metaData )
    throws ReportProcessingException {
    this( metaData, report.getResourceBundleFactory(), report.getConfiguration(), report.getResourceManager(), report
        .getContentBase(), report.getBundle().getMetaData(), report.getReportEnvironment(), -1 );

    final Integer comLev = report.getCompatibilityLevel();
    if ( comLev != null ) {
      compatibilityLevel = comLev;
    }
  }

  /**
   * @param outputProcessorMetaData
   * @param resourceBundleFactory
   * @param configuration
   * @param resourceManager
   * @param contentBase
   *          the content base, from where to load additional resources. (Can be null).
   * @param metaData
   */
  public DefaultProcessingContext( final OutputProcessorMetaData outputProcessorMetaData,
      final ResourceBundleFactory resourceBundleFactory, final Configuration configuration,
      final ResourceManager resourceManager, final ResourceKey contentBase, final DocumentMetaData metaData,
      final ReportEnvironment environment, final int compatibilityLevel ) throws ReportProcessingException {
    if ( environment == null ) {
      throw new NullPointerException();
    }
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    if ( outputProcessorMetaData == null ) {
      throw new NullPointerException();
    }
    if ( resourceBundleFactory == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.contentBase = contentBase;
    this.resourceManager = resourceManager;
    this.outputProcessorMetaData = outputProcessorMetaData;
    this.resourceBundleFactory = MasterReport.computeAndInitResourceBundleFactory( resourceBundleFactory, environment );
    this.formulaContext =
        DefaultFormulaContextFactory.INSTANCE.create( resourceBundleFactory.getLocale(), resourceBundleFactory
            .getTimeZone() );
    this.configuration = configuration;
    if ( metaData == null ) {
      this.metaData = new MemoryDocumentMetaData();
    } else {
      this.metaData = metaData;
    }
    this.reportEnvironment = new CachingReportEnvironment( environment );
    this.startTime = System.currentTimeMillis();
    this.compatibilityLevel = compatibilityLevel;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public ResourceKey getContentBase() {
    return contentBase;
  }

  public int getProgressLevel() {
    return progressLevel;
  }

  public void setProgressLevel( final int progressLevel ) {
    this.progressLevel = progressLevel;
  }

  public int getProgressLevelCount() {
    return progressLevelCount;
  }

  public void setProgressLevelCount( final int progressLevelCount ) {
    this.progressLevelCount = progressLevelCount;
  }

  public void setProcessingLevel( final int processingLevel ) {
    this.processingLevel = processingLevel;
  }

  public int getProcessingLevel() {
    return processingLevel;
  }

  public FormulaContext getFormulaContext() {
    return formulaContext;
  }

  public void setPrepareRun( final boolean prepareRun ) {
    this.prepareRun = prepareRun;
  }

  public boolean isPrepareRun() {
    return prepareRun;
  }

  public String getExportDescriptor() {
    return outputProcessorMetaData.getExportDescriptor();
  }

  public OutputProcessorMetaData getOutputProcessorMetaData() {
    return outputProcessorMetaData;
  }

  protected void setOutputProcessorMetaData( final OutputProcessorMetaData outputProcessorMetaData ) {
    if ( outputProcessorMetaData == null ) {
      throw new NullPointerException();
    }
    this.outputProcessorMetaData = outputProcessorMetaData;
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Returns the outermost master-report's document meta-data.
   *
   * @return the document meta-data.
   */
  public DocumentMetaData getDocumentMetaData() {
    return metaData;
  }

  public ReportEnvironment getEnvironment() {
    return reportEnvironment;
  }

  public long getReportProcessingStartTime() {
    return startTime;
  }

  public int getCompatibilityLevel() {
    return compatibilityLevel;
  }
}
