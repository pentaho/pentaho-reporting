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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class XmlPageReportUtil {
  private static final Log logger = LogFactory.getLog( XmlPageReportUtil.class );

  private XmlPageReportUtil() {
  }

  /**
   * Saves a report to XML format.
   *
   * @param report
   *          the report.
   * @param fileName
   *          target file.
   * @return true if the report has been successfully exported, false otherwise.
   */
  public static boolean createXml( final MasterReport report, final File fileName ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( fileName == null ) {
      throw new NullPointerException();
    }
    OutputStream out = null;
    try {
      out = new BufferedOutputStream( new FileOutputStream( fileName ) );
      createXml( report, out );
      out.close();
      out = null;
      return true;
    } catch ( Exception e ) {
      logger.error( "Writing XML failed.", e );
      return false;
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        logger.error( "Saving XML failed.", e );
      }
    }
  }

  /**
   * Saves a report to XML format.
   *
   * @param report
   *          the report.
   * @param out
   *          target output stream.
   * @return true if the report has been successfully exported, false otherwise.
   */
  public static boolean createXml( final MasterReport report, final OutputStream out ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }
    PageableReportProcessor proc = null;
    try {

      final XmlPageOutputProcessor outputProcessor = new XmlPageOutputProcessor( report.getConfiguration(), out );
      proc = new PageableReportProcessor( report, outputProcessor );
      proc.processReport();
      return true;
    } catch ( Exception e ) {
      logger.error( "Writing XML failed.", e );
      return false;
    } finally {
      if ( proc != null ) {
        proc.close();
      }
    }
  }

  /**
   * Concates and saves a list of reports to XML format.
   *
   * @param report
   *          the report.
   * @param fileName
   *          target file name.
   * @return true if the report has been successfully exported, false otherwise.
   */
  public static boolean createXml( final MasterReport report, final String fileName ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( fileName == null ) {
      throw new NullPointerException();
    }
    return createXml( report, new File( fileName ) );
  }

}
