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
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

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
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      ExcelReportUtil.createXLS( report, out );
      return;
    }

    final FastExcelExportProcessor reportProcessor = new FastExcelExportProcessor( report, out, false );
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }

  public static void processXlsx( final MasterReport report, final OutputStream out ) throws ReportProcessingException,
    IOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      ExcelReportUtil.createXLSX( report, out );
      return;
    }

    final FastExcelExportProcessor reportProcessor = new FastExcelExportProcessor( report, out, true );
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }


  public static void processXls( final MasterReport report, final OutputStream out, final ReportProgressListener listener ) throws ReportProcessingException,
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

  public static void processXlsx( final MasterReport report, final OutputStream out, final ReportProgressListener listener ) throws ReportProcessingException,
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
