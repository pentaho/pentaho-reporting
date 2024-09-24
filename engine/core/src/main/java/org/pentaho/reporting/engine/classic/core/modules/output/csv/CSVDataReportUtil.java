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

package org.pentaho.reporting.engine.classic.core.modules.output.csv;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Utility class to provide an easy to use default implementation of CSV table exports.
 *
 * @author Thomas Morgner
 */
public final class CSVDataReportUtil {
  /**
   * DefaultConstructor.
   */
  private CSVDataReportUtil() {
  }

  /**
   * Saves a report to CSV format.
   *
   * @param report
   *          the report.
   * @param writer
   *          the writer
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
   *           if the report processing failed.
   */
  public static void createCSV( final MasterReport report, final Writer writer ) throws ReportProcessingException {
    final CSVProcessor pr = new CSVProcessor( report );
    pr.setWriter( writer );
    pr.processReport();
  }

  /**
   * Saves a report to CSV format.
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
  public static void createCSV( final MasterReport report, final String filename ) throws ReportProcessingException,
    IOException {
    final String encoding =
        report.getConfiguration().getConfigProperty( CSVProcessor.CSV_ENCODING,
            EncodingRegistry.getPlatformDefaultEncoding() );
    createCSV( report, filename, encoding );
  }

  /**
   * Saves a report to CSV format.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @param encoding
   *          the encoding that should be used.
   * @throws org.pentaho.reporting.engine.classic.core.ReportProcessingException
   *           if the report processing failed.
   * @throws java.io.IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createCSV( final MasterReport report, final String filename, final String encoding )
    throws ReportProcessingException, IOException {
    final CSVProcessor pr = new CSVProcessor( report );
    final FileOutputStream outstr = new FileOutputStream( filename );
    final Writer fout = new BufferedWriter( new OutputStreamWriter( outstr, encoding ) );
    pr.setWriter( fout );
    pr.processReport();
    fout.close();
  }

  public static void createCSV( final MasterReport report, final OutputStream outputStream, final String encoding )
    throws ReportProcessingException, IOException {
    final CSVProcessor pr = new CSVProcessor( report );
    final Writer fout = new BufferedWriter( new OutputStreamWriter( outputStream, encoding ) );
    pr.setWriter( fout );
    pr.processReport();
    fout.flush();
  }

}
