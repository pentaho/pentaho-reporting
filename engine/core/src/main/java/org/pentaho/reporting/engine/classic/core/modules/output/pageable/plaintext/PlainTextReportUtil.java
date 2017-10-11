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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * An utility class to write an report into a plain text file. If you need more control over the writing process, you
 * will have to implement your own write method.
 *
 * @author Thomas Morgner
 */
public final class PlainTextReportUtil {
  /**
   * Default Constructor.
   */
  private PlainTextReportUtil() {
  }

  /**
   * Saves a report to plain text format.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @param charsPerInch
   *          chars per inch for the output.
   * @param linesPerInch
   *          lines per inch for the output.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createTextFile( final MasterReport report, final String filename, final float charsPerInch,
      final float linesPerInch ) throws IOException, ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    OutputStream fout = null;
    try {
      fout = new BufferedOutputStream( new FileOutputStream( filename ) );

      final TextFilePrinterDriver pc = new TextFilePrinterDriver( fout, charsPerInch, linesPerInch );
      final String lineSeparator = report.getReportConfiguration().getConfigProperty( "line.separator", "\n" );
      pc.setEndOfLine( lineSeparator.toCharArray() );
      pc.setEndOfPage( lineSeparator.toCharArray() );

      final PageableTextOutputProcessor outputProcessor =
          new PageableTextOutputProcessor( pc, report.getConfiguration() );
      final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
      proc.processReport();
      proc.close();
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

  public static void createTextFile( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    final Configuration configuration = report.getConfiguration();
    final String cpiText =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.CharsPerInch" );
    final String lpiText =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.LinesPerInch" );

    createTextFile( report, filename, ParserUtil.parseInt( cpiText, 10 ), ParserUtil.parseInt( lpiText, 6 ) );
  }

  public static void createPlainText( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    final Configuration configuration = report.getConfiguration();
    final String cpiText =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.CharsPerInch" );
    final String lpiText =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.LinesPerInch" );

    createPlainText( report, filename, ParserUtil.parseInt( cpiText, 10 ), ParserUtil.parseInt( lpiText, 6 ) );
  }

  /**
   * Saves a report to plain text format.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @param charsPerInch
   *          chars per inch for the output.
   * @param linesPerInch
   *          lines per inch for the output.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createPlainText( final MasterReport report, final String filename, final float charsPerInch,
      final float linesPerInch ) throws IOException, ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }
    OutputStream fout = null;
    try {
      fout = new BufferedOutputStream( new FileOutputStream( filename ) );
      createPlainText( report, fout, charsPerInch, linesPerInch, null );
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

  public static void createPlainText( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    final Configuration configuration = report.getConfiguration();
    final String cpiText =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.CharsPerInch" );
    final String lpiText =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.LinesPerInch" );
    createPlainText( report, outputStream, ParserUtil.parseInt( cpiText, 10 ), ParserUtil.parseInt( lpiText, 6 ), null );
  }

  public static void createPlainText( final MasterReport report, final OutputStream outputStream,
      final float charsPerInch, final float linesPerInch ) throws ReportProcessingException {
    createPlainText( report, outputStream, charsPerInch, linesPerInch, null );
  }

  public static void createPlainText( final MasterReport report, final OutputStream outputStream,
      final float charsPerInch, final float linesPerInch, final String encoding ) throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final TextFilePrinterDriver pc = new TextFilePrinterDriver( outputStream, charsPerInch, linesPerInch );
    final PageableTextOutputProcessor outputProcessor = new PageableTextOutputProcessor( pc, report.getConfiguration() );
    outputProcessor.setEncoding( encoding );

    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();
    proc.close();
  }

  public static byte[] getInitSequence( final Configuration report ) throws UnsupportedEncodingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    final String encoding =
        report.getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.RawEncoding", "Raw" );
    final String sequence =
        report
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.RawInitSequence" );
    if ( sequence == null ) {
      return null;
    }
    if ( "Raw".equalsIgnoreCase( encoding ) ) {
      final char[] rawChars = sequence.toCharArray();
      final int rawCharLength = rawChars.length;
      final byte[] rawBytes = new byte[rawCharLength];
      for ( int i = 0; i < rawCharLength; i++ ) {
        rawBytes[i] = (byte) rawChars[i];
      }
      return rawBytes;
    } else {
      return sequence.getBytes( encoding );
    }
  }

}
