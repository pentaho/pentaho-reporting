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

package org.pentaho.reporting.engine.classic.core.modules.gui.html;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

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
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FlowHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.PageableHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.SingleRepositoryURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;

/**
 * An export task implementation that exports the report into a ZIPped Html directory structure.
 *
 * @author Thomas Morgner
 */
public class HtmlZipExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( HtmlZipExportTask.class );
  /**
   * Provides access to externalized strings
   */
  private Messages messages;

  /**
   * The progress dialog that monitors the export process.
   */
  private final ReportProgressDialog progressDialog;
  /**
   * The report that should be exported.
   */
  private final MasterReport report;
  private StatusListener statusListener;

  private String exportMethod;
  private String dataDirectory;
  private File targetFile;

  /**
   * Creates a new html export task.
   *
   * @param dialog
   *          the progress monitor component.
   * @param report
   *          the report that should be exported.
   */
  public HtmlZipExportTask( final MasterReport report, final ReportProgressDialog dialog,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new ReportProcessingException( "HtmlZipExportTask(..): Report-Parameter cannot be null" ); //$NON-NLS-1$
    }

    final Configuration config = report.getConfiguration();
    dataDirectory =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.DataDirectory" ); //$NON-NLS-1$
    final String targetFileName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.TargetFileName" ); //$NON-NLS-1$
    exportMethod =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.ExportMethod" ); //$NON-NLS-1$

    this.progressDialog = dialog;
    this.report = report;
    if ( swingGuiContext != null ) {
      this.statusListener = swingGuiContext.getStatusListener();
      this.messages =
          new Messages( swingGuiContext.getLocale(), HtmlExportGUIModule.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( HtmlExportGUIModule.class ) );
    } else {
      this.messages =
          new Messages( Locale.getDefault(), HtmlExportGUIModule.BASE_RESOURCE_CLASS, ObjectUtilities
              .getClassLoader( HtmlExportGUIModule.class ) );
    }

    targetFile = new File( targetFileName );

    if ( targetFile.exists() ) {
      // lets try to delete it ..
      if ( targetFile.delete() == false ) {
        throw new ReportProcessingException( messages
            .getErrorString( "HtmlZipExportTask.ERROR_0002_TARGET_FILE_EXISTS" ) ); //$NON-NLS-1$
      }
    }
  }

  /**
   * Exports the report into a Html Directory Structure.
   */
  public void run() {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream( new FileOutputStream( targetFile ) );

      final ZipRepository zipRepository = new ZipRepository( out );
      final ContentLocation root = zipRepository.getRoot();
      final ContentLocation data =
          RepositoryUtilities.createLocation( zipRepository, RepositoryUtilities.splitPath( dataDirectory, "/" ) ); //$NON-NLS-1$

      final ReportProcessor sp;
      if ( "pageable".equals( exportMethod ) ) { //$NON-NLS-1$
        final PageableHtmlOutputProcessor outputProcessor = new PageableHtmlOutputProcessor( report.getConfiguration() );
        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( root, new DefaultNameGenerator( root, "report.html" ) ); //$NON-NLS-1$
        printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) ); //$NON-NLS-1$
        printer.setUrlRewriter( new SingleRepositoryURLRewriter() );
        outputProcessor.setPrinter( printer );
        sp = new PageableReportProcessor( report, outputProcessor );
      } else {
        final HtmlOutputProcessor outputProcessor = createOutputProcessor();
        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( root, new DefaultNameGenerator( root, "report.html" ) ); //$NON-NLS-1$
        printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) ); //$NON-NLS-1$
        printer.setUrlRewriter( new SingleRepositoryURLRewriter() );
        outputProcessor.setPrinter( printer );
        sp = new FlowReportProcessor( report, outputProcessor );
      }
      if ( progressDialog != null ) {
        progressDialog.setModal( false );
        progressDialog.setVisible( true );
        sp.addReportProgressListener( progressDialog );
      }
      sp.processReport();
      sp.close();

      zipRepository.close();
      out.close();
      out = null;

      if ( progressDialog != null ) {
        sp.removeReportProgressListener( progressDialog );
      }

      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "HtmlZipExportTask.USER_TASK_FINISHED" ), null ); //$NON-NLS-1$
      }
    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "HtmlZipExportTask.USER_TASK_ABORTED" ), null ); //$NON-NLS-1$
      }
      try {
        out.close();
        out = null;
      } catch ( IOException ioe ) {
        // ignore me...
      }
    } catch ( Exception re ) {
      HtmlZipExportTask.logger.error( "Exporting failed .", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "HtmlZipExportTask.USER_TASK_FAILED" ), re ); //$NON-NLS-1$
      }
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        HtmlZipExportTask.logger.error( "Unable to close the output stream.", e ); //$NON-NLS-1$
        if ( statusListener != null ) {
          statusListener.setStatus( StatusType.ERROR, messages.getString( "HtmlZipExportTask.USER_TASK_FAILED" ), e ); //$NON-NLS-1$
        }
      }
    }
    if ( progressDialog != null ) {
      progressDialog.setVisible( false );
    }
  }

  protected HtmlOutputProcessor createOutputProcessor() {
    if ( "pageable".equals( exportMethod ) ) { //$NON-NLS-1$
      return new PageableHtmlOutputProcessor( report.getConfiguration() );
    } else if ( "flow".equals( exportMethod ) ) { //$NON-NLS-1$
      return new FlowHtmlOutputProcessor();
    } else {
      return new StreamHtmlOutputProcessor( report.getConfiguration() );
    }
  }
}
