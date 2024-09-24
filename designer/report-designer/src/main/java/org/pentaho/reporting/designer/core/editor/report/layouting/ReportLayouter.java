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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;

/**
 * Single-instance layouter for handling the layout computation for a single report. This layouts the full report with
 * all bands. The no-data-band will be layouted in parallel to the itemband.
 */
public class ReportLayouter {
  private LogicalPageBox logicalPageBox;
  private ReportDocumentContext reportRenderContext;
  private long lastModCount;
  private DesignerOutputProcessorMetaData metaData;
  private DesignerRenderComponentFactory componentFactory;

  public ReportLayouter( final ReportRenderContext reportRenderContext ) {
    this.reportRenderContext = reportRenderContext;
    this.lastModCount = 0;
  }

  public LogicalPageBox layout() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = reportRenderContext.getContextRoot();
    if ( logicalPageBox != null && lastModCount == report.getChangeTracker() ) {
      return logicalPageBox;
    }

    if ( componentFactory == null ) {
      componentFactory = new DesignerRenderComponentFactory( getOutputProcessorMetaData() );
    }

    final DesignerOutputProcessor outputProcessor = new DesignerOutputProcessor( getOutputProcessorMetaData() );
    final DesignerReportProcessor reportProcessor =
      new DesignerReportProcessor( report, outputProcessor, componentFactory );
    reportProcessor.processReport();
    this.logicalPageBox = outputProcessor.getLogicalPage();
    lastModCount = report.getChangeTracker();
    return logicalPageBox;
  }

  public synchronized DesignerOutputProcessorMetaData getOutputProcessorMetaData() {
    if ( metaData == null ) {
      final DesignerOutputProcessorMetaData designerOutputProcessorMetaData = new DesignerOutputProcessorMetaData();
      designerOutputProcessorMetaData.initialize( reportRenderContext.getContextRoot().getConfiguration() );
      metaData = designerOutputProcessorMetaData;
    }
    return metaData;
  }
}
