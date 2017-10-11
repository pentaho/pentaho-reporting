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

package org.pentaho.reporting.engine.classic.core.modules.gui.csv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportInterruptedException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.output.csv.CSVProcessor;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An export task implementation that writes an report into a CSV file, and uses the raw target to create layouted
 * content.
 *
 * @author Thomas Morgner
 */
public class CSVDataExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( CSVDataExportTask.class );
  /**
   * Provides access to externalized strings
   */
  private Messages messages;
  /**
   * The name of the output file.
   */
  private final String fileName;

  /**
   * The report that should be exported.
   */
  private final MasterReport report;
  private ReportProgressDialog progressDialog;
  private StatusListener statusListener;

  /**
   * Creates a new CSV export task.
   *
   * @param report
   *          the report that should be exported.
   * @param dialog
   *          the progress dialog to inform the user about the report progress.
   * @param swingGuiContext
   *          the context connecting the task to the outside UI.
   */
  public CSVDataExportTask( final MasterReport report, final ReportProgressDialog dialog,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException( "CSVDataExportTask(..): Report parameter cannot be null" ); //$NON-NLS-1$
    }

    final String filename =
        report.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.gui.csv.FileName" ); //$NON-NLS-1$
    if ( filename == null ) {
      throw new ReportProcessingException( "CSVDataExportTask(..): Configuration does not contain a valid filename" ); //$NON-NLS-1$
    }

    this.fileName = filename;
    this.report = report;
    this.progressDialog = dialog;
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), CSVDataExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( CSVDataExportPlugin.class ) );
    }
  }

  /**
   * Exports the report into a CSV file.
   */
  public void run() {
    Writer out = null;
    final File file = new File( fileName );
    try {
      final File directory = file.getAbsoluteFile().getParentFile();
      if ( directory != null ) {
        if ( directory.exists() == false ) {
          if ( directory.mkdirs() == false ) {
            CSVDataExportTask.logger.warn( "Can't create directories. Hoping and praying now.." ); //$NON-NLS-1$
          }
        }
      }

      final String encoding =
          report.getConfiguration().getConfigProperty( CSVProcessor.CSV_ENCODING,
              EncodingRegistry.getPlatformDefaultEncoding() );
      out = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), encoding ) );

      final CSVProcessor target = new CSVProcessor( report );
      if ( progressDialog != null ) {
        progressDialog.setModal( false );
        progressDialog.setVisible( true );
        target.addReportProgressListener( progressDialog );
      }
      target.setWriter( out );
      target.processReport();
      out.close();
      out = null;

      if ( progressDialog != null ) {
        target.removeReportProgressListener( progressDialog );
      }

      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "CSVRawExportTask.USER_TASK_COMPLETE" ), null ); //$NON-NLS-1$
      }
    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "CSVRawExportTask.USER_TASK_ABORTED" ), null ); //$NON-NLS-1$
      }
      try {
        out.close();
        out = null;
        if ( file.delete() == false ) {
          CSVDataExportTask.logger.warn( "Unable to delete incomplete export:" + file ); //$NON-NLS-1$
        }
      } catch ( SecurityException se ) {
        // ignore me
      } catch ( IOException ioe ) {
        // ignore me...
      }
    } catch ( Exception re ) {
      CSVDataExportTask.logger.error( "Exporting failed .", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "CSVRawExportTask.USER_TASK_FAILED" ), re ); //$NON-NLS-1$
      }
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        if ( statusListener != null ) {
          statusListener.setStatus( StatusType.ERROR, messages.getString( "CSVRawExportTask.USER_TASK_FAILED" ), e ); //$NON-NLS-1$
        }
        CSVDataExportTask.logger.error( "Unable to close the output stream.", e ); //$NON-NLS-1$
      }

      if ( progressDialog != null ) {
        progressDialog.setVisible( false );
      }
    }
  }
}
