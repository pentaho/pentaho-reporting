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

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to provide an easy to use default implementation of CSV table exports.
 *
 * @author Thomas Morgner
 */
public final class CSVReportUtil {
  /**
   * DefaultConstructor.
   */
  private CSVReportUtil() {
  }

  /**
   * Saves a report to CSV format.
   *
   * @param report
   *          the report.
   * @param outputStream
   *          the output stream.
   * @param encoding
   *          the encoding for the output stream (can be null).
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if an IO related error occured.
   */
  public static void createCSV( final MasterReport report, final OutputStream outputStream, final String encoding )
    throws ReportProcessingException, IOException {
    createCSV( report, outputStream, encoding, null );
  }



  /**
   * Saves a report to CSV format.
   *
   * @param report
   *          the report.
   * @param outputStream
   *          the output stream.
   * @param encoding
   *          the encoding for the output stream (can be null).
   *
   * @param listener listener
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if an IO related error occured.
   */
  public static void createCSV( final MasterReport report, final OutputStream outputStream, final String encoding, final ReportProgressListener listener )
          throws ReportProcessingException, IOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final StreamCSVOutputProcessor target = new StreamCSVOutputProcessor( outputStream );
    if ( encoding != null ) {
      target.setEncoding( encoding );
    }

    final StreamReportProcessor reportProcessor = new StreamReportProcessor( report, target );
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
    outputStream.flush();
  }

  /**
   * Saves a report to CSV format.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createCSV( final MasterReport report, final String filename ) throws ReportProcessingException,
    IOException {
    createCSV( report, filename, EncodingRegistry.getPlatformDefaultEncoding() );
  }

  /**
   * Saves a report to CSV format.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @param encoding
   *          the optional encoding that should be used.
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
   *           if the report processing failed.
   * @throws java.io.IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createCSV( final MasterReport report, final String filename, final String encoding )
    throws ReportProcessingException, IOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }
    final OutputStream fout = new BufferedOutputStream( new FileOutputStream( filename ) );
    try {
      createCSV( report, fout, encoding );
    } finally {
      fout.close();
    }
  }

}
