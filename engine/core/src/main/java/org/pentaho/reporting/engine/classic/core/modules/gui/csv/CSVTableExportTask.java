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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.csv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportInterruptedException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.ReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.csv.FastCsvExportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.StreamCSVOutputProcessor;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An export task implementation that writes an report into a CSV file, and uses the table target to create layouted
 * content.
 *
 * @author Thomas Morgner
 */
public class CSVTableExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( CSVTableExportTask.class );
  /**
   * Provides access to externalized strings
   */
  private Messages messages;
  /**
   * The progress dialog that monitors the export process.
   */
  private final ReportProgressDialog progressDialog;
  /**
   * The name of the output file.
   */
  private final String fileName;

  /**
   * The report that should be exported.
   */
  private final MasterReport report;
  private StatusListener statusListener;
  private boolean createParentFolder;
  /**
   * Creates a new CSV export task.
   *
   * @param swingGuiContext
   *          the GUI Context
   * @param dialog
   *          the progress monitor
   * @param report
   *          the report that should be exported.
   * @throws ReportProcessingException
   *           if the report did not define a valid filename.
   */
  public CSVTableExportTask( final MasterReport report, final ReportProgressDialog dialog,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException( "CSVTableExportTask(..): Report parameter cannot be null" ); //$NON-NLS-1$
    }

    final String filename =
        report.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.gui.csv.FileName" ); //$NON-NLS-1$
    if ( filename == null ) {
      throw new ReportProcessingException( "CSVTableExportTask(..): Configuration does not contain a valid filename" ); //$NON-NLS-1$
    }

    final String createParentFolder =
      report.getConfiguration().getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.csv.CreateParentFolder" ); //$NON-NLS-1$
    if ( createParentFolder == null ) {
      this.createParentFolder = false;
    } else {
      this.createParentFolder = Boolean.parseBoolean( createParentFolder );
    }

    this.progressDialog = dialog;
    this.report = report;
    this.fileName = filename;
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), CSVTableExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( CSVTableExportPlugin.class ) );
    }
  }

  /**
   * Exports the report into a CSV file.
   */
  public void run() {
    OutputStream out = null;

    final File file = new File( fileName );
    try {
      if ( createParentFolder ) {
        final File directory = file.getAbsoluteFile().getParentFile();
        if ( directory != null ) {
          if ( directory.exists() == false ) {
            if ( directory.mkdirs() == false ) {
              CSVTableExportTask.logger.warn( "Can't create directories. Hoping and praying now.." ); //$NON-NLS-1$
            }
          }
        }
      }
      out = new BufferedOutputStream( new FileOutputStream( file ) );

      ReportProcessor reportProcessor;
      ReportStructureValidator validator = new ReportStructureValidator();
      if ( validator.isValidForFastProcessing( report ) == false ) {
        final StreamCSVOutputProcessor target = new StreamCSVOutputProcessor( out );
        reportProcessor = new StreamReportProcessor( report, target );
      } else {
        reportProcessor = new FastCsvExportProcessor( report, out );
      }

      if ( progressDialog != null ) {
        progressDialog.setModal( false );
        progressDialog.setVisible( true );
        reportProcessor.addReportProgressListener( progressDialog );
      }
      reportProcessor.processReport();
      out.close();
      out = null;
      if ( progressDialog != null ) {
        reportProcessor.removeReportProgressListener( progressDialog );
      }

      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "CSVTableExportTask.USER_TASK_COMPLETE" ), null ); //$NON-NLS-1$
      }
    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "CSVTableExportTask.USER_TASK_ABORTED" ), null ); //$NON-NLS-1$
      }

      try {
        out.close();
        out = null;
        if ( file.delete() == false ) {
          CSVTableExportTask.logger.warn( "Unable to delete incomplete export:" + file ); //$NON-NLS-1$
        }
      } catch ( SecurityException se ) {
        // ignore me
      } catch ( IOException ioe ) {
        // ignore me...
      }
    } catch ( Exception re ) {
      CSVTableExportTask.logger.error( "Exporting failed .", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "CSVTableExportTask.USER_TASK_FAILED" ), re ); //$NON-NLS-1$
      }
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        CSVTableExportTask.logger.error( "Unable to close the output stream.", e ); //$NON-NLS-1$
        // if there is already another error, this exception is
        // just a minor obstactle. Something big crashed before ...
      }

      if ( progressDialog != null ) {
        progressDialog.setVisible( false );
      }
    }
  }
}
