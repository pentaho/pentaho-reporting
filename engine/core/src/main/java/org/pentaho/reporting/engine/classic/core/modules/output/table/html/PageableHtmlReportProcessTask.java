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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.AbstractMultiStreamReportProcessTask;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class PageableHtmlReportProcessTask extends AbstractMultiStreamReportProcessTask {
  public PageableHtmlReportProcessTask() {
  }

  /**
   * @noinspection ThrowableInstanceNeverThrown
   */
  public void run() {
    if ( isValid() == false ) {
      setError( new ReportProcessingException( "Error: The task is not configured properly." ) );
      return;
    }

    setError( null );
    try {
      final MasterReport masterReport = getReport();
      final Configuration configuration = masterReport.getConfiguration();

      final HtmlPrinter printer = new AllItemsHtmlPrinter( masterReport.getResourceManager() );
      printer.setContentWriter( getBodyContentLocation(), getBodyNameGenerator() );
      printer.setDataWriter( getBulkLocation(), getBulkNameGenerator() );
      printer.setUrlRewriter( computeUrlRewriter() );

      final PageableHtmlOutputProcessor outputProcessor = new PageableHtmlOutputProcessor( configuration );
      final PageableReportProcessor streamReportProcessor = new PageableReportProcessor( masterReport, outputProcessor );
      try {
        final ReportProgressListener[] progressListeners = getReportProgressListeners();
        for ( int i = 0; i < progressListeners.length; i++ ) {
          final ReportProgressListener listener = progressListeners[i];
          streamReportProcessor.addReportProgressListener( listener );
        }
        streamReportProcessor.processReport();
      } finally {
        streamReportProcessor.close();
      }
    } catch ( Throwable e ) {
      setError( e );
    }
  }

  public String getReportMimeType() {
    return "text/html";
  }
}
