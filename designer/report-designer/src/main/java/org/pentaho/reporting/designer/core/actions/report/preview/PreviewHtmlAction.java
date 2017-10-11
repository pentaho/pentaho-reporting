/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.actions.report.preview;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.output.ReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewParametersDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
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
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.file.FileRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public final class PreviewHtmlAction extends AbstractReportContextAction {
  public PreviewHtmlAction() {
    putValue( Action.NAME, ActionMessages.getString( "PreviewHtmlAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "PreviewHtmlAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "PreviewHtmlAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "PreviewHtmlAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getPreviewHTMLIcon() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( getActiveContext() == null ) {
      return;
    }

    final MasterReport reportElement = getActiveContext().getContextRoot();
    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( PreviewParametersDialog.process( window, reportElement ) ) {
      final ReportProgressDialog dialog;
      if ( window instanceof JDialog ) {
        dialog = new ReportProgressDialog( (JDialog) window );
      } else if ( window instanceof JFrame ) {
        dialog = new ReportProgressDialog( (JFrame) window );
      } else {
        dialog = new ReportProgressDialog();
      }

      final Thread t = new Thread( new ExportTask( reportElement, dialog ) );
      t.setDaemon( true );
      t.start();
    }
  }

  private static class ExportTask implements Runnable {
    private MasterReport report;
    private ReportProgressDialog progressDialog;

    private ExportTask( final MasterReport report,
                        final ReportProgressDialog progressDialog ) {
      this.report = report;
      this.progressDialog = progressDialog;
    }

    public void run() {
      try {
        final File tempDir = createTemporaryDirectory( "report-designer-html-preview" );//$NON-NLS-1$

        try {
          final FileRepository targetRepository = new FileRepository( tempDir );
          final ContentLocation targetRoot = targetRepository.getRoot();


          ReportProcessor reportProcessor;
          ReportStructureValidator validator = new ReportStructureValidator();
          if ( validator.isValidForFastProcessing( report ) == false ) {
            final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor( report.getConfiguration() );
            final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
            printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
            printer.setDataWriter( targetRoot, new DefaultNameGenerator( targetRoot, "content" ) ); //$NON-NLS-1$
            printer.setUrlRewriter( new FileSystemURLRewriter() );
            outputProcessor.setPrinter( printer );
            reportProcessor = new StreamReportProcessor( report, outputProcessor );
          } else {
            FastHtmlContentItems printer = new FastHtmlContentItems();
            printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
            printer.setDataWriter( targetRoot, new DefaultNameGenerator( targetRoot, "content" ) ); //$NON-NLS-1$
            printer.setUrlRewriter( new FileSystemURLRewriter() );
            reportProcessor = new FastHtmlExportProcessor( report, printer );
          }

          reportProcessor.addReportProgressListener( progressDialog );
          progressDialog.setVisibleInEDT( true );

          reportProcessor.processReport();
          reportProcessor.close();

          reportProcessor.removeReportProgressListener( progressDialog );
          progressDialog.setVisibleInEDT( false );

          final File previewFile = new File( tempDir, "index.html" );//$NON-NLS-1$
          ExternalToolLauncher.openURL( previewFile.toURI().toURL().toExternalForm() );
        } catch ( final Exception e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
          progressDialog.dispose();
          final String errorMessage = ActionMessages.getString( "PreviewReport.Error.Text" );
          final String errorTitle = ActionMessages.getString( "PreviewReport.Error.Title" );
          ExceptionDialog.showExceptionDialog( progressDialog.getParent(), errorTitle, errorMessage, e1 );
        }

        tempDir.deleteOnExit();
        markDirectoryContentAsDeletable( tempDir );
      } catch ( final IOException e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }
  }

  protected static File createTemporaryDirectory( final String directoryName ) throws IOException {
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String s = configuration.getConfigProperty( "java.io.tmpdir" );//NON-NLS
    final File tempDir = new File( s );
    if ( tempDir.exists() == false ) {
      tempDir.mkdirs();
    }
    if ( tempDir.exists() == false || tempDir.isDirectory() == false ) {
      throw new IOException( "Unable to access or create the temp-directory" );
    }
    if ( tempDir.canWrite() == false ) {
      throw new IOException( "Unable to write to temp-directory." );
    }

    final Random randomGenerator = new Random( System.currentTimeMillis() );
    for ( int i = 1; i < 200; i++ ) {
      final int random = ( randomGenerator.nextInt() );
      final File reportDirectory = new File( s, directoryName + random );

      if ( reportDirectory.exists() && reportDirectory.isDirectory() == false ) {
        continue;
      }
      if ( !reportDirectory.exists() && !reportDirectory.mkdirs() ) {
        continue;
      }

      reportDirectory.deleteOnExit();
      return reportDirectory;
    }

    throw new IOException( "Unable to generate the target directory." );
  }

  protected static void markDirectoryContentAsDeletable( final File directory ) {
    final File[] files = directory.listFiles();
    if ( files != null ) {
      for ( final File file : files ) {
        file.deleteOnExit();
        if ( file.isDirectory() ) {
          markDirectoryContentAsDeletable( file );
        }
      }
    }
  }

}
