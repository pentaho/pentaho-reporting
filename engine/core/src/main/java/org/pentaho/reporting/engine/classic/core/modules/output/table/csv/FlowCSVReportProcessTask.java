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

package org.pentaho.reporting.engine.classic.core.modules.output.table.csv;

import org.pentaho.reporting.engine.classic.core.AbstractMultiStreamReportProcessTask;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

public class FlowCSVReportProcessTask extends AbstractMultiStreamReportProcessTask {
  public FlowCSVReportProcessTask() {
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

      final ContentLocation contentLocation = getBodyContentLocation();
      final NameGenerator nameGenerator = getBodyNameGenerator();

      final FlowCSVOutputProcessor outputProcessor = new FlowCSVOutputProcessor( configuration );
      outputProcessor.setContentLocation( contentLocation );
      outputProcessor.setContentNameGenerator( nameGenerator );

      final FlowReportProcessor streamReportProcessor = new FlowReportProcessor( masterReport, outputProcessor );
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
    return "text/csv";
  }
}
