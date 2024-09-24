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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;

import java.io.IOException;
import java.io.OutputStream;

public class FastExcelReportUtil {
  private FastExcelReportUtil() {
  }

  public static void processXls( final MasterReport report, final OutputStream out ) throws ReportProcessingException,
    IOException {
    processXls( report, out, null );
  }

  public static void processXlsx( final MasterReport report, final OutputStream out ) throws ReportProcessingException,
    IOException {
    processXlsx( report, out, null );
  }


  public static void processXls( final MasterReport report, final OutputStream out,
                                 final ReportProgressListener listener ) throws ReportProcessingException,
    IOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      ExcelReportUtil.createXLS( report, out, listener );
      return;
    }

    final FastExcelExportProcessor reportProcessor = new FastExcelExportProcessor( report, out, false );
    if ( listener != null ) {
      reportProcessor.addReportProgressListener( listener );
    }
    doProcess( listener, reportProcessor );
    out.flush();
  }

  public static void processXlsx( final MasterReport report, final OutputStream out,
                                  final ReportProgressListener listener ) throws ReportProcessingException,
    IOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      ExcelReportUtil.createXLSX( report, out, listener );
      return;
    }

    final FastExcelExportProcessor reportProcessor = new FastExcelExportProcessor( report, out, true );
    if ( listener != null ) {
      reportProcessor.addReportProgressListener( listener );
    }
    doProcess( listener, reportProcessor );
    out.flush();
  }

  private static void doProcess( final ReportProgressListener listener, final FastExcelExportProcessor reportProcessor )
    throws ReportProcessingException {
    try {
      reportProcessor.processReport();
    } finally {
      if ( listener != null ) {
        reportProcessor.removeReportProgressListener( listener );
      }
      reportProcessor.close();
    }
  }
}
