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


package org.pentaho.reporting.engine.classic.core.modules.gui.xls;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelExportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * An export task implementation, which writes a given report into an Excel file.
 *
 * @author Thomas Morgner
 */
public class XSSFExcelExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( XSSFExcelExportTask.class );
  /**
   * Access to externalized strings
   */
  private Messages messages;

  /**
   * The progress dialog that will be used to visualize the report progress.
   */
  private final ReportProgressDialog progressDialog;
  /**
   * The file name of the output file.
   */
  private final String fileName;
  /**
   * The report which should be exported.
   */
  private final MasterReport report;
  private StatusListener statusListener;
  private boolean createParentFolder;
  /**
   * Creates a new export task.
   *
   * @param dialog
   *          the progress dialog that will monitor the report progress.
   * @param report
   *          the report that should be exported.
   */
  public XSSFExcelExportTask( final MasterReport report, final ReportProgressDialog dialog,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException( "ExcelExportTask(..): Null report parameter not permitted" ); //$NON-NLS-1$
    }
    this.fileName =
        report.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.gui.xls.FileName" ); //$NON-NLS-1$
    if ( fileName == null ) {
      throw new ReportProcessingException( "ExcelExportTask(): Filename is not defined" ); //$NON-NLS-1$
    }
    this.progressDialog = dialog;
    this.report = report;
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), ExcelExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( ExcelExportPlugin.class ) );
    }
    final String createParentFolder =
      report.getConfiguration().getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.xls.CreateParentFolder" ); //$NON-NLS-1$
    if ( createParentFolder == null ) {
      this.createParentFolder = false;
    } else {
      this.createParentFolder = Boolean.parseBoolean( createParentFolder );
    }
  }

  /**
   * Exports the report into an Excel file.
   */
  @Override
  public void run() {
    OutputStream out = null;
    File file = null;
    try {
      file = new File( fileName ).getCanonicalFile();
      if ( createParentFolder ) {
        final File directory = file.getParentFile();
        if ( directory != null ) {
          if ( directory.exists() == false ) {
            if ( directory.mkdirs() == false ) {
              XSSFExcelExportTask.logger.warn( "Can't create directories. Hoping and praying now.." ); //$NON-NLS-1$
            }
          }
        }
      }
      out = new BufferedOutputStream( new FileOutputStream( file ) );

      ReportStructureValidator validator = new ReportStructureValidator();
      ReportProcessor reportProcessor;
      if ( validator.isValidForFastProcessing( report ) == false ) {
        final FlowExcelOutputProcessor target =
            new FlowExcelOutputProcessor( report.getConfiguration(), out, report.getResourceManager() );
        target.setUseXlsxFormat( true );
        reportProcessor = new FlowReportProcessor( report, target );
      } else {
        reportProcessor = new FastExcelExportProcessor( report, out, true );
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
            messages.getString( "ExcelExportTask.USER_TASK_FINISHED" ), null ); //$NON-NLS-1$
      }

    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.WARNING, messages.getString( "ExcelExportTask.USER_TASK_ABORTED" ), null ); //$NON-NLS-1$
      }

      try {
        out.close();
        out = null;
        if ( file.delete() == false ) {
          XSSFExcelExportTask.logger.warn( "Unable to delete incomplete export:" + file ); //$NON-NLS-1$
        }
      } catch ( SecurityException se ) {
        // ignore me
      } catch ( IOException ioe ) {
        // ignore me...
      }
    } catch ( Exception re ) {
      XSSFExcelExportTask.logger.error( "Excel export failed", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "ExcelExportTask.USER_TASK_FAILED" ), re ); //$NON-NLS-1$
      }
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        XSSFExcelExportTask.logger.error( "Unable to close the output stream.", e ); //$NON-NLS-1$
        if ( statusListener != null ) {
          statusListener.setStatus( StatusType.WARNING, messages.getString( "ExcelExportTask.USER_TASK_FAILED" ), e ); //$NON-NLS-1$
        }
        // if there is already another error, this exception is
        // just a minor obstactle. Something big crashed before ...
      }
    }
    if ( progressDialog != null ) {
      progressDialog.setVisible( false );
    }
  }
}
