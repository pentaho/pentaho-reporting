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

package org.pentaho.reporting.designer.core.editor.styles.styleeditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.editor.styles.Messages;
import org.pentaho.reporting.designer.core.status.ExceptionDialog;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.StyleDefinitionWriter;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StyleDefinitionUtilities {
  private static final String DEFAULT_EXTENSION = ".prptstyle";
  private static final String FILE_CHOOSER_TYPE = "style-definition";
  private static final Log logger = LogFactory.getLog( StyleDefinitionUtilities.class );

  private StyleDefinitionUtilities() {
  }


  /**
   * Prompts the user for the name of the report file which should be created
   *
   * @param parent      the parent component of which the file chooser dialog will be a child
   * @param defaultFile the initially selected file.
   * @return The <code>File</code> which the report should be saved into, or <code>null</code> if the user does not want
   * to continue with the save operation
   */
  public static File promptReportFilename( final Component parent, final File defaultFile ) {
    final FileFilter filter = new FilesystemFilter
      ( new String[] { DEFAULT_EXTENSION },
        Messages.getString( "StyleDefinitionUtilities.FileDescription" ), true );

    final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( FILE_CHOOSER_TYPE );
    fileChooser.setSelectedFile( defaultFile );
    fileChooser.setFilters( new FileFilter[] { filter } );
    logger.debug( "Prompting for save filename" ); // NON-NLS
    if ( fileChooser.showDialog( parent, JFileChooser.SAVE_DIALOG ) == false ) {
      logger.debug( "Save filename - cancel option selected" );// NON-NLS
      return null;
    }


    final File selectedFile = validateFileExtension( fileChooser.getSelectedFile(), parent );
    if ( selectedFile == null ) {
      // Cancel on another dialog
      return null;
    }

    // Once the filename has stabelized, check for overwrite
    if ( selectedFile.exists() ) {
      logger.debug( "Selected file exists [" + selectedFile.getName() + "] - prompting for overwrite..." );// NON-NLS
      final int overwrite = JOptionPane.showConfirmDialog( parent,
        Messages.getString( "StyleDefinitionUtilities.OverwriteDialog.Message",
          selectedFile.getAbsolutePath() ), Messages.getString( "StyleDefinitionUtilities.OverwriteDialog.Title" ),
        JOptionPane.YES_NO_OPTION );
      if ( overwrite == JOptionPane.NO_OPTION ) {
        return null;
      }
    }

    return selectedFile;
  }

  public static boolean saveStyleDefinitionAs( final StyleDefinitionEditorContext activeContext,
                                               final Component parent ) {
    // Get the current file target
    final File defaultFile = activeContext.getSource();

    // Prompt for the filename
    final File target = promptReportFilename( parent, defaultFile );
    if ( target == null ) {
      return false;
    }

    activeContext.setSource( target );


    // Save the report
    if ( saveStyleDefinition( activeContext, target ) ) {
      return true;
    }

    final ExceptionDialog exceptionDialog;
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    if ( window instanceof Dialog ) {
      exceptionDialog = new ExceptionDialog( (Dialog) window );
    } else if ( window instanceof Frame ) {
      exceptionDialog = new ExceptionDialog( (Frame) window );
    } else {
      exceptionDialog = new ExceptionDialog();
    }
    exceptionDialog.showDialog();
    return false;
  }

  public static boolean saveStyleDefinition( final StyleDefinitionEditorContext activeContext,
                                             final Component parent ) {
    // Get the current file target
    File target = activeContext.getSource();

    // If there is no target, this file has not been save before ... prompt for a filename
    if ( target == null ) {
      target = promptReportFilename( parent, null );
    } else {
      target = validateFileExtension( target, parent );
    }
    if ( target == null ) {
      return false;
    }

    // if no name has been set for the report, default to the name of the file
    activeContext.setSource( target );

    // Write the report to the filename
    if ( saveStyleDefinition( activeContext, target ) ) {
      return true;
    }

    ExceptionDialog.showDialog( parent );
    return false;
  }


  /**
   * Performs the action of saving the report to the specified target file
   *
   * @param activeContext the active render context
   * @param target        the target file to which the report will be saved
   * @return true, if saving was successful, false otherwise.
   */
  private static boolean saveStyleDefinition( final StyleDefinitionEditorContext activeContext,
                                              final File target ) {
    if ( activeContext == null ) {
      throw new IllegalArgumentException();
    }
    if ( target == null ) {
      throw new IllegalArgumentException();
    }

    try {
      // Save the report to the specified file
      logger.debug( "Saving report in filename [" + target.getAbsolutePath() + "]" );// NON-NLS
      final StyleDefinitionWriter writer = new StyleDefinitionWriter();
      writer.write( target, activeContext.getStyleDefinition() );
      return true;
    } catch ( Exception e1 ) {
      UncaughtExceptionsModel.getInstance().addException
        ( new IOException( Messages.getString( "StyleDefinitionUtilities.SaveFailed.Message" ), e1 ) );// NON-NLS
      logger.error( "Failed to save report", e1 );// NON-NLS
      return false;
    }
  }

  /**
   * Validates that the extension of the filename is prpt, and prompts the user if it is not.
   *
   * @param proposedFile the target file to validate
   * @param parent       the parent component in case we need to display a dialog
   * @return the filename based on the validation and optional prompting, or <code>null</code> if the user decided to
   * cancel the operaion
   */
  public static File validateFileExtension( final File proposedFile, final Component parent ) {
    if ( proposedFile == null ) {
      return null;
    }

    // See if we need to change the file extension
    final String s = proposedFile.getName();
    if ( s.endsWith( DEFAULT_EXTENSION ) ) {
      return proposedFile;
    }

    final String extension = IOUtils.getInstance().getFileExtension( s );
    if ( "".equals( extension ) ) {
      final File parentFile = proposedFile.getParentFile();
      if ( parentFile == null ) {
        return new File( IOUtils.getInstance().stripFileExtension( s ) + DEFAULT_EXTENSION );
      } else {
        return new File( parentFile, IOUtils.getInstance().stripFileExtension( s ) + DEFAULT_EXTENSION );
      }
    }

    logger.debug( "The selected filename does not have the standard extension - " +// NON-NLS
      "prompting the user to see if they want to change the extension" );// NON-NLS
    final int result = JOptionPane.showConfirmDialog( parent,
      Messages.getString( "StyleDefinitionUtilities.VerifyFileExtension.Message",
        proposedFile.getAbsolutePath() ), Messages.getString( "StyleDefinitionUtilities.VerifyFileExtension.Title" ),
      JOptionPane.YES_NO_CANCEL_OPTION );
    if ( result == JOptionPane.CANCEL_OPTION ) {
      return null;
    }
    if ( result == JOptionPane.NO_OPTION ) {
      return proposedFile;
    }

    final File validatedFile =
      new File( proposedFile.getParent(), IOUtils.getInstance().stripFileExtension( s ) + DEFAULT_EXTENSION );
    logger
      .debug( "User has selected YES - the filename has been changed to [" + validatedFile.getName() + "]" );// NON-NLS
    return validatedFile;
  }


  /**
   * Invoked when an action occurs.
   */
  public static void openStyleDefinition( final StyleDefinitionEditorContext context ) {
    final FileFilter filter = new FilesystemFilter
      ( new String[] { DEFAULT_EXTENSION },
        Messages.getString( "StyleDefinitionUtilities.FileDescription" ), true );

    final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( FILE_CHOOSER_TYPE );
    fileChooser.setFilters( new FileFilter[] { filter } );
    fileChooser.setAllowMultiSelection( true );
    if ( fileChooser.showDialog( context.getParent(), JFileChooser.OPEN_DIALOG ) == false ) {
      return;
    }
    final File[] selectedFiles = fileChooser.getSelectedFiles();
    for ( int i = 0, selectedFilesLength = selectedFiles.length; i < selectedFilesLength; i++ ) {
      final File selectedFile = selectedFiles[ i ];
      SwingUtilities.invokeLater( new OpenReportTask( selectedFile, context ) );
    }
  }

  public static class OpenReportTask implements Runnable {
    private File selectedFile;
    private StyleDefinitionEditorContext context;

    public OpenReportTask( final File selectedFile, final StyleDefinitionEditorContext context ) {
      this.selectedFile = selectedFile;
      this.context = context;
    }

    public void run() {
      openReport( selectedFile, context );
    }

    private static void openReport( final File selectedFile,
                                    final StyleDefinitionEditorContext context ) {
      if ( selectedFile == null ) {
        throw new NullPointerException();
      }
      if ( context == null ) {
        throw new NullPointerException();
      }

      final LoadStyleDefinitionTask target = new LoadStyleDefinitionTask( selectedFile );
      final Thread loadThread = new Thread( target );
      loadThread.setDaemon( true );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog
        ( loadThread, null, context.getParent(), Messages.getString( "StyleDefinitionUtilities.LoadMessage" ) );
      final ElementStyleDefinition report = target.getStyleDefinition();
      if ( report != null ) {
        context.setStyleDefinition( report );
        context.setSource( selectedFile );
      } else {
        final Exception exception = target.getException();
        if ( exception instanceof ResourceCreationException ) {
          UncaughtExceptionsModel.getInstance().addException( exception );
          ExceptionDialog.showDialog( context.getParent() );
        } else if ( exception != null ) {
          UncaughtExceptionsModel.getInstance().addException( exception );
          ExceptionDialog.showDialog( context.getParent() );
        }
      }
    }
  }


  private static class LoadStyleDefinitionTask implements Runnable {
    private File file;
    private ElementStyleDefinition styleDefinition;
    private Exception exception;

    private LoadStyleDefinitionTask( final File file ) {
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
        final ResourceManager mgr = new ResourceManager();
        final Resource directly = mgr.createDirectly( file, ElementStyleDefinition.class );
        styleDefinition = (ElementStyleDefinition) directly.getResource();
      } catch ( Exception e ) {
        this.exception = e;
      }
    }

    public ElementStyleDefinition getStyleDefinition() {
      return styleDefinition;
    }

    public Exception getException() {
      return exception;
    }
  }

}
