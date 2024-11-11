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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xml;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal.XmlTableOutputProcessorMetaData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to provide an easy to use default implementation of xml exports.
 *
 * @author Thomas Morgner
 */
public final class XmlTableReportUtil {
  /**
   * DefaultConstructor.
   */
  private XmlTableReportUtil() {
  }

  /**
   * Saves a report into a single XML file.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
   *           if the report processing failed.
   * @throws java.io.IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createStreamXML( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    final File file = new File( filename );
    final OutputStream fout = new BufferedOutputStream( new FileOutputStream( file ) );
    try {
      createStreamXML( report, fout );
    } finally {
      fout.close();
    }
  }

  public static void createStreamXML( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final XmlTableOutputProcessor outputProcessor =
        new XmlTableOutputProcessor( outputStream, new XmlTableOutputProcessorMetaData(
            XmlTableOutputProcessorMetaData.PAGINATION_NONE ) );
    final StreamReportProcessor sp = new StreamReportProcessor( report, outputProcessor );
    sp.processReport();
    sp.close();
  }

  public static void createFlowXML( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final XmlTableOutputProcessor outputProcessor =
        new XmlTableOutputProcessor( outputStream, new XmlTableOutputProcessorMetaData(
            XmlTableOutputProcessorMetaData.PAGINATION_MANUAL ) );
    final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
    sp.processReport();
    sp.close();
  }
}
