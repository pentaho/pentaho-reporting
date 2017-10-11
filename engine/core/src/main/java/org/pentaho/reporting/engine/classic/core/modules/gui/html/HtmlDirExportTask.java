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

import java.io.File;
import java.io.IOException;
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
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FlowHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.PageableHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.file.FileRepository;

/**
 * An export task implementation that exports the report into a HTML directory structure.
 *
 * @author Thomas Morgner
 */
public class HtmlDirExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( HtmlDirExportTask.class );
  /**
   * Provides access to externalized strings
   */
  private Messages messages;

  /**
   * The progress dialog that monitors the export process.
   */
  private final ReportProgressDialog progressDialog;

  /**
   * The name of the data directory (relative to the target file).
   */
  private File dataDirectory;
  /**
   * The report that should be exported.
   */
  private final MasterReport report;
  private StatusListener statusListener;
  private String exportMethod;
  private File targetDirectory;
  private String suffix;
  private String filename;

  /**
   * Creates a new html export task.
   *
   * @param progressDialog
   *          the progress monitor component (may be null).
   * @param report
   *          the report that should be exported.
   */
  public HtmlDirExportTask( final MasterReport report, final ReportProgressDialog progressDialog,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new ReportProcessingException( "HtmlDirExportTask(..): Report-Parameter cannot be null" ); //$NON-NLS-1$
    }

    this.progressDialog = progressDialog;
    this.report = report;

    final Configuration config = report.getConfiguration();
    final String dataDirectoryName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.file.DataDirectory" ); //$NON-NLS-1$
    final String targetFileName =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.file.TargetFileName" ); //$NON-NLS-1$
    exportMethod =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.file.ExportMethod" ); //$NON-NLS-1$

    try {
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

      final File targetFile = new File( targetFileName ).getCanonicalFile();
      targetDirectory = targetFile.getParentFile();

      final File tempDataDir = new File( dataDirectoryName ).getCanonicalFile();
      if ( "".equals( dataDirectoryName ) == false && tempDataDir.isAbsolute() ) {
        dataDirectory = tempDataDir;
      } else {
        dataDirectory = new File( targetDirectory, dataDirectoryName ).getCanonicalFile();
      }
      if ( dataDirectory.exists() && dataDirectory.isDirectory() == false ) {
        dataDirectory = dataDirectory.getParentFile();
        if ( dataDirectory.isDirectory() == false ) {
          throw new ReportProcessingException( "HtmlDirExportTask(..): Data-Directory is invalid: " + dataDirectory ); //$NON-NLS-1$
        }
      } else if ( dataDirectory.exists() == false ) {
        dataDirectory.mkdirs();
      }

      suffix = getSuffix( targetFileName );
      filename = IOUtils.getInstance().stripFileExtension( targetFile.getName() );

      if ( targetFile.exists() ) {
        // lets try to delete it ..
        if ( targetFile.delete() == false ) {
          throw new ReportProcessingException( messages.getErrorString(
              "HtmlDirExportTask.ERROR_0003_TARGET_FILE_EXISTS", targetFile.getAbsolutePath() ) ); //$NON-NLS-1$
        }
      }
    } catch ( IOException ioe ) {
      throw new ReportProcessingException( "Failed to normalize directories.", ioe );
    }
  }

  private String getSuffix( final String filename ) {
    final String suffix = IOUtils.getInstance().getFileExtension( filename );
    if ( suffix.length() == 0 ) {
      return ""; //$NON-NLS-1$
    }
    return suffix.substring( 1 );
  }

  /**
   * Exports the report into a Html Directory Structure.
   */
  public void run() {
    try {

      final FileRepository targetRepository = new FileRepository( targetDirectory );
      final ContentLocation targetRoot = targetRepository.getRoot();

      final FileRepository dataRepository = new FileRepository( dataDirectory );
      final ContentLocation dataRoot = dataRepository.getRoot();
      final ReportProcessor sp;
      if ( "pageable".equals( exportMethod ) ) { //$NON-NLS-1$
        final PageableHtmlOutputProcessor outputProcessor = new PageableHtmlOutputProcessor( report.getConfiguration() );
        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, filename, suffix ) );
        printer.setDataWriter( dataRoot, new DefaultNameGenerator( dataRoot, "content" ) ); //$NON-NLS-1$
        printer.setUrlRewriter( new FileSystemURLRewriter() );
        outputProcessor.setPrinter( printer );
        sp = new PageableReportProcessor( report, outputProcessor );
      } else {
        final HtmlOutputProcessor outputProcessor = createOutputProcessor();
        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, filename, suffix ) );
        printer.setDataWriter( dataRoot, new DefaultNameGenerator( dataRoot, "content" ) ); //$NON-NLS-1$
        printer.setUrlRewriter( new FileSystemURLRewriter() );
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

      if ( progressDialog != null ) {
        sp.removeReportProgressListener( progressDialog );
      }

      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "HtmlDirExportTask.USER_TASK_FINISHED" ), null ); //$NON-NLS-1$
      }
    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION, messages.getString( "HtmlDirExportTask.USER_TASK_ABORTED" ),
            null ); //$NON-NLS-1$
      }
    } catch ( Exception re ) {
      HtmlDirExportTask.logger.error( "Exporting failed .", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "HtmlDirExportTask.USER_TASK_ERROR" ), re ); //$NON-NLS-1$
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
