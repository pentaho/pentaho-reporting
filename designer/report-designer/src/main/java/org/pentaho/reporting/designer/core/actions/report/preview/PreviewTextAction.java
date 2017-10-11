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
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewParametersDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PageableTextOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextPageableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.TextFilePrinterDriver;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class PreviewTextAction extends AbstractReportContextAction {
  public PreviewTextAction() {
    putValue( Action.NAME, ActionMessages.getString( "PreviewTextAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "PreviewTextAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "PreviewTextAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "PreviewTextAction.Accelerator" ) );
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
        final File tempFile = File.createTempFile( "report-designer-preview", ".txt" );//$NON-NLS-1$
        tempFile.deleteOnExit();
        final FileOutputStream fout = new FileOutputStream( tempFile );
        try {
          final BufferedOutputStream bout = new BufferedOutputStream( fout );
          final float charPerInch = ParserUtil.parseFloat( report.getReportConfiguration().getConfigProperty
            ( PlainTextPageableModule.CHARS_PER_INCH ), 10.0f );
          final float linesPerInch = ParserUtil.parseFloat( report.getReportConfiguration().getConfigProperty
            ( PlainTextPageableModule.LINES_PER_INCH ), 6.0f );

          final PageableTextOutputProcessor outputProcessor = new PageableTextOutputProcessor
            ( new TextFilePrinterDriver( bout, charPerInch, linesPerInch ), report.getConfiguration() );
          final PageableReportProcessor reportProcessor = new PageableReportProcessor( report, outputProcessor );

          reportProcessor.addReportProgressListener( progressDialog );
          progressDialog.setVisibleInEDT( true );

          reportProcessor.processReport();
          reportProcessor.close();
          bout.flush();
          reportProcessor.removeReportProgressListener( progressDialog );
        } finally {
          fout.close();
        }
        progressDialog.setVisibleInEDT( false );

        ExternalToolLauncher.openURL( tempFile.toURI().toURL().toExternalForm() );
      } catch ( Exception e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
        progressDialog.dispose();
        final String errorMessage = ActionMessages.getString( "PreviewReport.Error.Text" );
        final String errorTitle = ActionMessages.getString( "PreviewReport.Error.Title" );
        ExceptionDialog.showExceptionDialog( progressDialog.getParent(), errorTitle, errorMessage, e1 );
      }
    }
  }

}
