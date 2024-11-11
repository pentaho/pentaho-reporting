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


package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to provide an easy to use default implementation of RTF exports.
 *
 * @author Thomas Morgner
 */
public final class RTFReportUtil {
  /**
   * Default Constructor.
   */
  private RTFReportUtil() {
  }

  /**
   * Saves a report to rich-text format (RTF).
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
  public static void createRTF( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    OutputStream fout = new BufferedOutputStream( new FileOutputStream( filename ) );
    try {
      createRTF( report, fout );
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

  public static void createRTF( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final StreamRTFOutputProcessor target =
        new StreamRTFOutputProcessor( report.getConfiguration(), outputStream, report.getResourceManager() );
    final StreamReportProcessor proc = new StreamReportProcessor( report, target );
    proc.processReport();
    proc.close();
  }
}
