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

package org.pentaho.reporting.engine.classic.core.modules.gui.pdf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * An export task implementation which writes a given report into a PDF file.
 *
 * @author Thomas Morgner
 */
public class PdfExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( PdfExportTask.class );
  /**
   * Provides access to externalized strings
   */
  private Messages messages;

  private MasterReport report;
  private ReportProgressDialog progressListener;
  private StatusListener statusListener;
  private File targetFile;
  private boolean createParentFolder;

  /**
   * Creates a new PDF export task.
   */
  public PdfExportTask( final MasterReport report, final ReportProgressDialog progressListener,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException( "PdfExportTask(..): Report parameter cannot be null" );
    }

    this.report = report;
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), PdfExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( PdfExportPlugin.class ) );
    } else {
      this.messages =
          new Messages( Locale.getDefault(), PdfExportPlugin.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( PdfExportPlugin.class ) );
    }

    this.progressListener = progressListener;
    final Configuration config = report.getConfiguration();
    final String targetFileName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.TargetFileName" ); //$NON-NLS-1$
    if ( targetFileName == null ) {
      throw new NullPointerException( "TargetFileName must be set in the configuration." );
    }

    targetFile = new File( targetFileName );
    if ( targetFile.exists() ) {
      if ( targetFile.delete() == false ) {
        throw new ReportProcessingException( messages.getErrorString( "PdfExportTask.ERROR_0001_TARGET_EXISTS" ) ); //$NON-NLS-1$
      }
    }

    final String createParentFolder =
      config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.CreateParentFolder" ); //$NON-NLS-1$
    if ( createParentFolder == null ) {
      this.createParentFolder = false;
    } else {
      this.createParentFolder = Boolean.parseBoolean( createParentFolder );
    }
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    PageableReportProcessor proc = null;
    OutputStream fout = null;
    try {
      if ( createParentFolder ) {
        final File directory = targetFile.getAbsoluteFile().getParentFile();
        if ( directory != null ) {
          if ( directory.exists() == false ) {
            if ( directory.mkdirs() == false ) {
              PdfExportTask.logger.warn( "Can't create directories." ); //$NON-NLS-1$
            }
          }
        }
      }
      fout = new BufferedOutputStream( new FileOutputStream( targetFile ) );
      final PdfOutputProcessor outputProcessor =
          new PdfOutputProcessor( report.getConfiguration(), fout, report.getResourceManager() );
      proc = new PageableReportProcessor( report, outputProcessor );
      if ( progressListener != null ) {
        proc.addReportProgressListener( progressListener );
        progressListener.setVisible( true );
      }
      proc.processReport();
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "PdfExportTask.USER_EXPORT_COMPLETE" ), null ); //$NON-NLS-1$
      }
    } catch ( Exception e ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "PdfExportTask.USER_EXPORT_FAILED" ), e ); //$NON-NLS-1$
      }
      PdfExportTask.logger.error( "Failed" ); //$NON-NLS-1$
    } finally {
      if ( proc != null ) {
        if ( progressListener != null ) {
          proc.removeReportProgressListener( progressListener );
        }
        proc.close();
      }
      if ( fout != null ) {
        try {
          fout.close();
        } catch ( IOException e ) {
          // We tried our best ...
        }
      }

      if ( progressListener != null ) {
        progressListener.setVisible( false );
      }

    }
  }
}
