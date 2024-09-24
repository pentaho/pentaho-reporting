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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to provide an easy to use default implementation of PDF exports.
 *
 * @author Thomas Morgner
 * @author Cedric Pronzato
 */
public final class PdfReportUtil {
  private static final Log logger = LogFactory.getLog( PdfReportUtil.class );

  /**
   * DefaultConstructor.
   */
  private PdfReportUtil() {
  }

  /**
   * Saves a report to PDF format.
   *
   * @param report
   *          the report.
   * @param fileName
   *          target file.
   * @return true if the report has been successfully exported, false otherwise.
   */
  public static boolean createPDF( final MasterReport report, final File fileName ) throws ReportProcessingException,
    IOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( fileName == null ) {
      throw new NullPointerException();
    }

    OutputStream out = null;
    try {
      out = new BufferedOutputStream( new FileOutputStream( fileName ) );
      final boolean retval = createPDF( report, out );
      out.close();
      out = null;
      return retval;
    } catch ( ReportProcessingException rpe ) {
      throw rpe;
    } catch ( Exception e ) {
      throw new ReportProcessingException( "Writing PDF failed", e );
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( IOException e ) {
        logger.error( "Saving PDF failed.", e );
        throw e;
      }
    }
  }

  /**
   * Saves a report to PDF format.
   *
   * @param report
   *          the report.
   * @param out
   *          target output stream.
   * @return true if the report has been successfully exported, false otherwise.
   */
  public static boolean createPDF( final MasterReport report, final OutputStream out ) throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }

    PageableReportProcessor proc = null;
    try {

      final PdfOutputProcessor outputProcessor =
          new PdfOutputProcessor( report.getConfiguration(), out, report.getResourceManager() );
      proc = new PageableReportProcessor( report, outputProcessor );
      proc.processReport();
      return true;
    } catch ( ReportProcessingException rpe ) {
      throw rpe;
    } catch ( Exception e ) {
      throw new ReportProcessingException( "Writing PDF failed", e );
    } catch ( Error e ) {
      throw new ReportProcessingException( "Writing PDF failed", e );
    } finally {
      if ( proc != null ) {
        proc.close();
      }
    }
  }

  /**
   * Concates and saves a list of reports to PDF format.
   *
   * @param report
   *          the report.
   * @param fileName
   *          target file name.
   * @return true if the report has been successfully exported, false otherwise.
   */
  public static boolean createPDF( final MasterReport report, final String fileName ) throws ReportProcessingException,
    IOException {
    return createPDF( report, new File( fileName ) );
  }
}
