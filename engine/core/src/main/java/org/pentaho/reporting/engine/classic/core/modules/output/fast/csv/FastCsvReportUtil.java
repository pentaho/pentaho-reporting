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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;

import java.io.IOException;
import java.io.OutputStream;

public class FastCsvReportUtil {
  private FastCsvReportUtil() {
  }

  public static void process( MasterReport report, OutputStream out ) throws ReportProcessingException, IOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      CSVReportUtil.createCSV( report, out, null );
      return;
    }

    final FastCsvExportProcessor reportProcessor = new FastCsvExportProcessor( report, out );
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }


  public static void process( MasterReport report, OutputStream out, final ReportProgressListener listener ) throws ReportProcessingException, IOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      CSVReportUtil.createCSV( report, out, null, listener );
      return;
    }

    final FastCsvExportProcessor reportProcessor = new FastCsvExportProcessor( report, out );
    if ( listener != null ) {
      reportProcessor.addReportProgressListener( listener );
    }
    try {
      reportProcessor.processReport();
    } finally {
      if ( listener != null ) {
        reportProcessor.removeReportProgressListener( listener );
      }
      reportProcessor.close();
    }
    out.flush();
  }
}
