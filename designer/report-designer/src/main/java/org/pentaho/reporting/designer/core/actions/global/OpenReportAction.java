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

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.designtime.swing.ConsumableActionEvent;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public final class OpenReportAction extends AbstractDesignerContextAction {
  public OpenReportAction() {
    putValue( Action.NAME, ActionMessages.getString( "OpenReportAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "OpenReportAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "OpenReportAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "OpenReportAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getOpenIcon() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( e.getSource() instanceof MacOSXIntegration.ApplicationEventSupport ) {
      final MacOSXIntegration.ApplicationEventSupport integration =
        (MacOSXIntegration.ApplicationEventSupport) e.getSource();
      final String fileName = integration.getFileName();
      if ( fileName != null ) {
        SwingUtilities.invokeLater( new OpenReportTask( new File( fileName ), getReportDesignerContext() ) );
      }

      if ( e instanceof ConsumableActionEvent ) {
        final ConsumableActionEvent ce = (ConsumableActionEvent) e;
        ce.consume();
      }

      return;
    }

    final FileFilter filter = new FilesystemFilter
      ( new String[] { ".xml", ".report", ".prpt", ".prpti" }, // NON-NLS
        ActionMessages.getString( "OpenReportAction.FileTypeDescriptor" ), true );

    final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "report" );//NON-NLS
    fileChooser.setFilters( new FileFilter[] { filter } );
    fileChooser.setAllowMultiSelection( true );
    if ( fileChooser.showDialog( getReportDesignerContext().getView().getParent(), JFileChooser.OPEN_DIALOG )
      == false ) {
      return;
    }
    final File[] selectedFiles = fileChooser.getSelectedFiles();
    for ( final File selectedFile : selectedFiles ) {
      SwingUtilities.invokeLater( new OpenReportTask( selectedFile, getReportDesignerContext() ) );
    }
  }

  public static class OpenReportTask implements Runnable {
    private File selectedFile;
    private ReportDesignerContext context;

    public OpenReportTask( final File selectedFile, final ReportDesignerContext context ) {
      this.selectedFile = selectedFile;
      this.context = context;
    }

    public void run() {
      openReport( selectedFile, context );
      // Even if the database is not valid, never ever tell the user that the report definition
      // cannot be opened when in the next minute that same report pops up. 
      context.getView().setWelcomeVisible( false );
    }
  }


  public static void openReport( final File selectedFile,
                                 final ReportDesignerContext context ) {
    if ( selectedFile == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final LoadReportTask target = new LoadReportTask( selectedFile );
    final Thread loadThread = new Thread( target );
    loadThread.setDaemon( true );
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog
      ( loadThread, null, context.getView().getParent(),
        ActionMessages.getString( "OpenReportAction.LoadReportMessage" ) );
    final AbstractReportDefinition report = target.getReport();
    if ( report instanceof MasterReport ) {
      try {
        context.addMasterReport( (MasterReport) report );
        context.getRecentFilesModel().addFile( selectedFile );

        final ReportDocumentContext activeContext = context.getActiveContext();
        if ( activeContext != null ) {
          activeContext.resetChangeTracker();
        }
      } catch ( ReportDataFactoryException e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    } else {
      final Exception exception = target.getException();
      if ( exception instanceof ResourceCreationException ) {
        ExceptionDialog
          .showExceptionDialog( context.getView().getParent(), ActionMessages.getString( "FailedToOpen.Error.Title" ),
            ActionMessages.getString( "FailedToOpen.Error.Message" ), exception );
        UncaughtExceptionsModel.getInstance().addException( exception );
      } else if ( exception != null ) {
        UncaughtExceptionsModel.getInstance().addException( exception );
      }
    }

  }

  private static class LoadReportTask implements Runnable {
    private File file;
    private AbstractReportDefinition report;
    private Exception exception;

    private LoadReportTask( final File file ) {
      if ( file == null ) {
        throw new NullPointerException();
      }
      this.file = file;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      try {
        report = loadReport( file );
      } catch ( Exception e ) {
        this.exception = e;
      }
    }

    public AbstractReportDefinition getReport() {
      return report;
    }

    public Exception getException() {
      return exception;
    }
  }

  public static MasterReport loadReport( final File selectedFile )
    throws ResourceException, IOException {
    final ResourceManager resourceManager = new ResourceManager();
    final MasterReport reportDefinition = loadReport( selectedFile, resourceManager );
    try {
      reportDefinition.setAttribute
        ( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME, selectedFile.getCanonicalPath() ); // NON-NLS
    } catch ( IOException ioe ) {
      reportDefinition.setAttribute
        ( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME, selectedFile.getAbsolutePath() ); // NON-NLS
    }

    return reportDefinition;
  }

  public static MasterReport loadReport( final Object selectedFile, final ResourceManager resourceManager )
    throws ResourceException, IOException {
    final Resource directly = resourceManager.createDirectly( selectedFile, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();
    final DocumentBundle bundle = resource.getBundle();
    if ( bundle == null ) {
      // Ok, that should not happen if we work with the engine's parsers, but better safe than sorry.
      final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle( resource.getContentBase() );
      documentBundle.getWriteableDocumentMetaData().setBundleType( ClassicEngineBoot.BUNDLE_TYPE );
      resource.setBundle( documentBundle );
      resource.setContentBase( documentBundle.getBundleMainKey() );
    } else {
      final MemoryDocumentBundle mem = new MemoryDocumentBundle( resource.getContentBase() );
      BundleUtilities.copyStickyInto( mem, bundle );
      BundleUtilities.copyMetaData( mem, bundle );
      resource.setBundle( mem );
      resource.setContentBase( mem.getBundleMainKey() );
    }

    final Object visible =
      resource.getBundle().getMetaData().getBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "visible" );//NON-NLS
    if ( "true".equals( visible ) )//NON-NLS
    {
      resource.setAttribute( AttributeNames.Pentaho.NAMESPACE, "visible", Boolean.TRUE );//NON-NLS
    } else if ( "false".equals( visible ) )//NON-NLS
    {
      resource.setAttribute( AttributeNames.Pentaho.NAMESPACE, "visible", Boolean.FALSE );//NON-NLS
    }
    return resource;
  }
}
