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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
