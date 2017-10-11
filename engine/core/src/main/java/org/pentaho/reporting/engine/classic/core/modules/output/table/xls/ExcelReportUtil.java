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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to provide an easy to use default implementation of excel exports.
 *
 * @author Thomas Morgner
 */
public final class ExcelReportUtil {
  /**
   * DefaultConstructor.
   */
  private ExcelReportUtil() {
  }

  /**
   * Saves a report to Excel format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws ReportProcessingException if the report processing failed.
   * @throws IOException               if there was an IOerror while processing the report.
   */
  public static void createXLSX( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    OutputStream fout = new BufferedOutputStream( new FileOutputStream( filename ) );
    processFlowXlsx( report, fout );
  }

  /**
   * Saves a report to Excel format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @param strict   defines whether the strict layout mode should be activated.
   * @throws ReportProcessingException if the report processing failed.
   * @throws IOException               if there was an IOerror while processing the report.
   */
  public static void createXLSX( final MasterReport report, final String filename, final boolean strict )
    throws IOException, ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    report.getReportConfiguration().setConfigProperty(
      "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout", String.valueOf( strict ) );

    OutputStream fout = new BufferedOutputStream( new FileOutputStream( filename ) );
    processFlowXlsx( report, fout );
  }

  public static void createXLSX( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    createXLSX( report, outputStream, null );
  }

  /**
   * Saves a report to Excel format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @throws ReportProcessingException if the report processing failed.
   * @throws IOException               if there was an IOerror while processing the report.
   */
  public static void createXLS( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    OutputStream fout = new BufferedOutputStream( new FileOutputStream( filename ) );
    processFlowXls( report, fout );
  }

  private static void processFlowXls( final MasterReport report, OutputStream fout )
    throws ReportProcessingException, IOException {
    try {
      final FlowExcelOutputProcessor target =
        new FlowExcelOutputProcessor( report.getConfiguration(), fout, report.getResourceManager() );
      target.setUseXlsxFormat( false );
      final FlowReportProcessor reportProcessor = new FlowReportProcessor( report, target );
      reportProcessor.processReport();
      reportProcessor.close();
      fout.close();
      fout = null;
    } finally {
      if ( fout != null ) {
        try {
          fout.close();
        } catch ( Exception e ) {
          // ignore
        }
      }
    }
  }

  /**
   * Saves a report to Excel format.
   *
   * @param report   the report.
   * @param filename target file name.
   * @param strict   defines whether the strict layout mode should be activated.
   * @throws ReportProcessingException if the report processing failed.
   * @throws IOException               if there was an IOerror while processing the report.
   */
  public static void createXLS( final MasterReport report, final String filename, final boolean strict )
    throws IOException, ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    report.getReportConfiguration().setConfigProperty(
      "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout", String.valueOf( strict ) );

    OutputStream fout = new BufferedOutputStream( new FileOutputStream( filename ) );
    processFlowXls( report, fout );
  }

  public static void createXLS( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    createXLS( report, outputStream, null );
  }


  public static void createXLS( final MasterReport report, final OutputStream outputStream,
                                final ReportProgressListener listener )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final FlowExcelOutputProcessor target =
      new FlowExcelOutputProcessor( report.getConfiguration(), outputStream, report.getResourceManager() );
    target.setUseXlsxFormat( false );
    final FlowReportProcessor reportProcessor = new FlowReportProcessor( report, target );
    if ( listener != null ) {
      reportProcessor.addReportProgressListener( listener );
    }
    doProcess( listener, reportProcessor );
  }

  public static void createXLSX( final MasterReport report, final OutputStream outputStream,
                                 final ReportProgressListener listener )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final FlowExcelOutputProcessor target =
      new FlowExcelOutputProcessor( report.getConfiguration(), outputStream, report.getResourceManager() );
    target.setUseXlsxFormat( true );
    final FlowReportProcessor reportProcessor = new FlowReportProcessor( report, target );
    if ( listener != null ) {
      reportProcessor.addReportProgressListener( listener );
    }
    doProcess( listener, reportProcessor );
  }

  private static void doProcess( ReportProgressListener listener, FlowReportProcessor reportProcessor )
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

  private static void processFlowXlsx( final MasterReport report, OutputStream fout )
    throws ReportProcessingException, IOException {
    try {
      final FlowExcelOutputProcessor target =
        new FlowExcelOutputProcessor( report.getConfiguration(), fout, report.getResourceManager() );
      target.setUseXlsxFormat( true );
      final FlowReportProcessor reportProcessor = new FlowReportProcessor( report, target );
      reportProcessor.processReport();
      reportProcessor.close();
      fout.close();
      fout = null;
    } finally {
      if ( fout != null ) {
        try {
          fout.close();
        } catch ( Exception e ) {
          // ignore
        }
      }
    }
  }
}
