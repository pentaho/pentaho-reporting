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

package org.pentaho.reporting.engine.classic.core.modules.gui.plaintext;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportInterruptedException;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PageableTextOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextPageableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.AbstractEpsonPrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.Epson24PinPrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.Epson9PinPrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.IBMCompatiblePrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.PrinterDriver;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * An export task implementation that writes the report into a plain text file.
 *
 * @author Thomas Morgner
 */
public class PlainTextExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( PlainTextExportTask.class );
  /**
   * Provides access to externalized strings
   */
  private Messages messages;

  /**
   * The progress monitor component that visualizes the export progress.
   */
  private final ReportProgressDialog progressDialog;
  /**
   * The name of the target file.
   */
  private final String fileName;
  /**
   * The report that should be exported.
   */
  private final MasterReport report;
  /**
   * The desired export type, one of the constants defined in the PlainTextExportDialog.
   */
  private final int exportType;
  /**
   * The chars per inch for the export.
   */
  private final float charPerInch;
  /**
   * The lines per inch for the export.
   */
  private final float linesPerInch;

  private String printer;
  private StatusListener statusListener;

  /**
   * Creates a new plain text export task.
   *
   * @param dialog
   *          the progress monitor dialog.
   * @param report
   *          the report that should be exported.
   */
  public PlainTextExportTask( final MasterReport report, final ReportProgressDialog dialog,
      final SwingGuiContext swingGuiContext ) {
    if ( report == null ) {
      throw new NullPointerException( "PlainTextExportTask(): Report parameter must not be null" ); //$NON-NLS-1$
    }

    final Configuration config = report.getConfiguration();
    fileName = config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.FileName" ); //$NON-NLS-1$
    final String selectedPrinterText =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.ExportType" ); //$NON-NLS-1$
    if ( "9pin".equals( selectedPrinterText ) ) { //$NON-NLS-1$
      exportType = PlainTextExportDialog.TYPE_EPSON9_OUTPUT;
      printer = config.getConfigProperty( Epson9PinPrinterDriver.EPSON_9PIN_PRINTER_TYPE );
    } else if ( "24pin".equals( selectedPrinterText ) ) { //$NON-NLS-1$
      exportType = PlainTextExportDialog.TYPE_EPSON24_OUTPUT;
      printer = config.getConfigProperty( Epson24PinPrinterDriver.EPSON_24PIN_PRINTER_TYPE );
    } else if ( "ibm".equals( selectedPrinterText ) ) { //$NON-NLS-1$
      exportType = PlainTextExportDialog.TYPE_IBM_OUTPUT;
      printer = null;
    } else {
      exportType = PlainTextExportDialog.TYPE_PLAIN_OUTPUT;
      printer = null;
    }

    this.progressDialog = dialog;
    this.report = report;

    charPerInch =
        ParserUtil.parseFloat( report.getReportConfiguration().getConfigProperty(
            PlainTextPageableModule.CHARS_PER_INCH ), 10.0f );
    linesPerInch =
        ParserUtil.parseFloat( report.getReportConfiguration().getConfigProperty(
            PlainTextPageableModule.LINES_PER_INCH ), 6.0f );
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), PlainTextExportGUIModule.BUNDLE_NAME, ObjectUtilities
              .getClassLoader( PlainTextExportPlugin.class ) );
    }
  }

  /**
   * Returns the printer command set for the given report and export type.
   *
   * @param out
   *          the output stream.
   * @return The printer command set.
   */
  protected PrinterDriver getPrinterCommandSet( final OutputStream out ) {
    switch ( exportType ) {
      case PlainTextExportDialog.TYPE_PLAIN_OUTPUT: {
        return new TextFilePrinterDriver( out, charPerInch, linesPerInch );
      }
      case PlainTextExportDialog.TYPE_IBM_OUTPUT: {
        return new IBMCompatiblePrinterDriver( out, charPerInch, linesPerInch );
      }
      case PlainTextExportDialog.TYPE_EPSON9_OUTPUT: {
        final Epson9PinPrinterDriver driver = new Epson9PinPrinterDriver( out, charPerInch, linesPerInch, printer );
        applyFallbackEncoding( driver );
        return driver;
      }
      case PlainTextExportDialog.TYPE_EPSON24_OUTPUT: {
        final Epson24PinPrinterDriver driver = new Epson24PinPrinterDriver( out, charPerInch, linesPerInch, printer );
        applyFallbackEncoding( driver );
        return driver;
      }
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Epson targets need special care, sadly the old printers used their own code page schema, which is totally
   * incompatible with the modern ones. All epson printers that were manufactured before 1991/1992 suffer form this
   * weakness.
   *
   * @param driver
   *          the driver, which should be configured.
   */
  private void applyFallbackEncoding( final AbstractEpsonPrinterDriver driver ) {
    final String encoding =
        report.getReportConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.gui.plaintext.FallbackEncoding" ); //$NON-NLS-1$
    driver.setFallBackCharset( (byte) ParserUtil.parseInt( encoding, 0 ) );
  }

  /**
   * Exports the report into a plain text file.
   */
  public void run() {
    PageableReportProcessor proc = null;
    OutputStream out = null;
    File file = null;
    try {
      file = new File( fileName ).getCanonicalFile();
      final File directory = file.getParentFile();
      if ( directory != null ) {
        if ( directory.exists() == false ) {
          if ( directory.mkdirs() == false ) {
            PlainTextExportTask.logger.warn( "Can't create directories. Hoping and praying now.." ); //$NON-NLS-1$
          }
        }
      }
      out = new BufferedOutputStream( new FileOutputStream( file ) );

      final PageableTextOutputProcessor outputProcessor =
          new PageableTextOutputProcessor( getPrinterCommandSet( out ), report.getConfiguration() );
      proc = new PageableReportProcessor( report, outputProcessor );

      if ( progressDialog != null ) {
        progressDialog.setModal( false );
        progressDialog.setVisible( true );
        proc.addReportProgressListener( progressDialog );
      }

      proc.processReport();

      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "PlainTextExportTask.USER_TASK_FINISHED" ), null ); //$NON-NLS-1$
      }
    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.WARNING,
            messages.getString( "PlainTextExportTask.USER_TASK_ABORTED" ), null ); //$NON-NLS-1$
      }
      try {
        out.close();
        out = null;
        if ( file.delete() == false ) {
          PlainTextExportTask.logger.warn( "Unable to delete incomplete export:" + file ); //$NON-NLS-1$
        }
      } catch ( SecurityException se ) {
        // ignore me
      } catch ( IOException ioe ) {
        // ignore me...
      }
    } catch ( Exception re ) {
      PlainTextExportTask.logger.error( "PlainText export failed", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "PlainTextExportTask.USER_TASK_FAILED" ), re ); //$NON-NLS-1$
      }
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        PlainTextExportTask.logger.error( "Unable to close the output stream.", e ); //$NON-NLS-1$
        // if there is already another error, this exception is
        // just a minor obstactle. Something big crashed before ...
      }

      if ( progressDialog != null ) {
        proc.removeReportProgressListener( progressDialog );
      }

    }
    if ( progressDialog != null ) {
      progressDialog.setVisible( false );
    }
  }
}
