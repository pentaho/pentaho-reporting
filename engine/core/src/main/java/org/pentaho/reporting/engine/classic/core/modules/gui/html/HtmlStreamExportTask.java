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

package org.pentaho.reporting.engine.classic.core.modules.gui.html;

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
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlContentItems;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlExportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.file.FileRepository;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * An export task implementation that exports the report into a single HTML file.
 *
 * @author Thomas Morgner
 */
public class HtmlStreamExportTask implements Runnable {
  private static final Log logger = LogFactory.getLog( HtmlStreamExportTask.class );
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
  private File targetDirectory;
  private String suffix;
  private String filename;
  private boolean createParentFolder;
  /**
   * Creates a new html export task.
   *
   * @param dialog
   *          the progress monitor component.
   * @param report
   *          the report that should be exported.
   */
  public HtmlStreamExportTask( final MasterReport report, final ReportProgressDialog dialog,
      final SwingGuiContext swingGuiContext ) throws ReportProcessingException {
    if ( report == null ) {
      throw new ReportProcessingException( "HtmlStreamExportTask(..): Report-Parameter cannot be null" ); //$NON-NLS-1$
    }
    try {
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

      final Configuration config = report.getConfiguration();
      final String targetFileName =
          config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.stream.TargetFileName" ); //$NON-NLS-1$
      if ( targetFileName == null ) {
        throw new ReportProcessingException( messages.getErrorString( "HtmlStreamExportTask.ERROR_0002_TARGET_NOT_SET" ) ); //$NON-NLS-1$
      }
      final String createParentFolder =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.stream.CreateParentFolder" ); //$NON-NLS-1$
      if ( createParentFolder == null ) {
        this.createParentFolder = false;
      } else {
        this.createParentFolder = Boolean.parseBoolean( createParentFolder );
      }
      final File targetFile = new File( targetFileName ).getCanonicalFile();
      targetDirectory = targetFile.getParentFile();

      suffix = getSuffix( targetFileName );
      filename = IOUtils.getInstance().stripFileExtension( targetFile.getName() );

      if ( targetFile.exists() ) {
        // lets try to delete it ..
        if ( targetFile.delete() == false ) {
          throw new ReportProcessingException( messages.getErrorString(
              "HtmlStreamExportTask.ERROR_0003_TARGET_FILE_EXISTS", targetFile.getAbsolutePath() ) ); //$NON-NLS-1$
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
      if ( createParentFolder ) {
        final File directory = targetDirectory.getCanonicalFile();
        if ( directory != null ) {
          if ( directory.exists() == false ) {
            if ( directory.mkdirs() == false ) {
              HtmlStreamExportTask.logger.warn( "Can't create directories." ); //$NON-NLS-1$
            }
          }
        }
      }
      final FileRepository targetRepository = new FileRepository( targetDirectory );
      final ContentLocation targetRoot = targetRepository.getRoot();

      // final DummyRepository dataRepository = new DummyRepository();
      // final ContentLocation dataRoot = dataRepository.getRoot();
      ReportProcessor reportProcessor;
      ReportStructureValidator validator = new ReportStructureValidator();
      if ( validator.isValidForFastProcessing( report ) == false ) {
        final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor( report.getConfiguration() );
        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, filename, suffix ) );
        printer.setDataWriter( null, null ); //$NON-NLS-1$
        printer.setUrlRewriter( new FileSystemURLRewriter() );
        outputProcessor.setPrinter( printer );
        reportProcessor = new StreamReportProcessor( report, outputProcessor );
      } else {
        FastHtmlContentItems printer = new FastHtmlContentItems();
        printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, filename, suffix ) );
        printer.setDataWriter( null, null ); //$NON-NLS-1$
        printer.setUrlRewriter( new FileSystemURLRewriter() );
        reportProcessor = new FastHtmlExportProcessor( report, printer );
      }

      if ( progressDialog != null ) {
        progressDialog.setModal( false );
        progressDialog.setVisible( true );
        reportProcessor.addReportProgressListener( progressDialog );
      }
      reportProcessor.processReport();

      if ( progressDialog != null ) {
        reportProcessor.removeReportProgressListener( progressDialog );
      }

      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION, messages
            .getString( "HtmlStreamExportTask.USER_TASK_FINISHED" ), null ); //$NON-NLS-1$);
      }
    } catch ( ReportInterruptedException re ) {
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.INFORMATION,
            messages.getString( "HtmlStreamExportTask.USER_TASK_ABORTED" ), null ); //$NON-NLS-1$);
      }
    } catch ( Exception re ) {
      HtmlStreamExportTask.logger.error( "Exporting failed .", re ); //$NON-NLS-1$
      if ( statusListener != null ) {
        statusListener.setStatus( StatusType.ERROR, messages.getString( "HtmlStreamExportTask.USER_TASK_ERROR" ), re ); //$NON-NLS-1$);
      }
    }
    if ( progressDialog != null ) {
      progressDialog.setVisible( false );
    }
  }
}
