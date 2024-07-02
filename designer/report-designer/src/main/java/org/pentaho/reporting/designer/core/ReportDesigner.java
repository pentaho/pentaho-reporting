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
* Copyright (c) 2024 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.editor.expressions.ExpressionUtil;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionsTreeModel;
import org.pentaho.reporting.designer.core.settings.SettingsUtil;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.settings.prefs.BinaryPreferencesFactory;
import org.pentaho.reporting.designer.core.splash.SplashScreen;
import org.pentaho.reporting.designer.core.status.ExceptionDialog;
import org.pentaho.reporting.designer.core.util.exceptions.ThrowableHandler;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.firewall.FirewallingProxySelector;
import org.pentaho.reporting.designer.core.util.firewall.FirewallingSecurityManager;
import org.pentaho.reporting.designer.core.welcome.SamplesTreeBuilder;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.util.ImageUtils;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.ColorPropertyEditor;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.net.ProxySelector;

public class ReportDesigner {
  private static class SetLookAndFeelTask implements Runnable {
    public void run() {
      int indent = 0;
      try {
        final String lnfName = WorkspaceSettings.getInstance().getLNF();
        if ( !StringUtils.isEmpty( lnfName ) ) {
          final LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
          for ( final LookAndFeelInfo lnf : lnfs ) {
            if ( lnf.getName().equals( lnfName ) ) {
              UIManager.setLookAndFeel( lnf.getClassName() );
              return;
            }
          }
        }

        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        indent = 5; //PRD-4583
      } catch ( Throwable t ) {
        UncaughtExceptionsModel.getInstance().addException( t );
      }

      final UIDefaults uiDefaults = UIManager.getDefaults();
      uiDefaults.put( "Table.gridColor", uiDefaults.get( "Panel.background" ) );// NON-NLS
      uiDefaults.put( "Tree.leftChildIndent", indent );//PRD-4419
    }
  }

  private static SplashScreen splashScreen;
  private static ReportDesignerFrame reportDesignerFrame;

  /**
   * Shows the splashscreen.
   */
  private static class InitializeSplashScreenTask implements Runnable {
    private InitializeSplashScreenTask() {
    }

    public void run() {
      final SplashScreen splashScreen = new SplashScreen();
      ReportDesigner.splashScreen = splashScreen;

      if ( WorkspaceSettings.getInstance().isSplashScreenVisible() ) {
        splashScreen.setVisible( true );
      }
    }
  }

  private static class UpdateStatusTask implements Runnable {
    private String status;

    private UpdateStatusTask( final String status ) {
      this.status = status;
    }

    public void run() {
      splashScreen.setStatus( status );
    }
  }

  private static class CreateReportDesignerFrame implements Runnable {
    private File[] files;

    private CreateReportDesignerFrame( final File[] files ) {
      this.files = files;
    }

    public void run() {
      try {
        final ReportDesignerFrame frame = new ReportDesignerFrame();
        ReportDesigner.reportDesignerFrame = frame;
        frame.pack();

        final Rectangle bounds = WorkspaceSettings.getInstance().getBounds();
        if ( bounds == null || LibSwingUtil.safeRestoreWindow( frame, bounds ) == false ) {
          LibSwingUtil.centerFrameOnScreen( frame );
          frame.setExtendedState( Frame.MAXIMIZED_BOTH );
        }
        frame.initWindowLocations( files );
        frame.setVisible( true );
      } catch ( Exception t ) {
        if ( splashScreen != null ) {
          splashScreen.dispose();
        }
        UncaughtExceptionsModel.getInstance().addException( t );
        final ExceptionDialog dialog = new ExceptionDialog();
        dialog.setModal( true );
        dialog.showDialog();
        System.exit( -1 );
      }
    }
  }


  private static class InstallAWTHandlerRunnable implements Runnable {
    private InstallAWTHandlerRunnable() {
    }

    public void run() {
      Thread.currentThread().setUncaughtExceptionHandler( ThrowableHandler.getInstance() );
    }
  }

  private ReportDesigner() {
  }

  /**
   * @noinspection ThrowableResultOfMethodCallIgnored
   */
  public static void main( final String[] args ) {
    boolean offlineMode = false;
    boolean offlineModeSecurityManager = false;

    int parsePos;
    for ( parsePos = 0; parsePos < args.length; parsePos += 1 ) {
      final String arg = args[ parsePos ];
      if ( "--offline".equals( arg ) || "-o".equals( arg ) ) // NON-NLS
      {
        offlineMode = true;
        continue;
      }
      if ( "--with-offline-mode-security-manager".equals( arg ) )// NON-NLS
      {
        offlineModeSecurityManager = true;
        continue;
      }
      break;
    }

    final File[] files = new File[ args.length - parsePos ];
    for ( int i = 0; i < args.length; i++ ) {
      final String arg = args[ i ];
      files[ i ] = new File( arg );
    }

    System.setProperty( "sun.awt.exception.handler", ThrowableHandler.class.getName() );// NON-NLS
    Thread.setDefaultUncaughtExceptionHandler( ThrowableHandler.getInstance() );

    System.setProperty( "java.util.prefs.PreferencesFactory", BinaryPreferencesFactory.class.getName() );// NON-NLS
    System.setProperty( "sun.swing.enableImprovedDragGesture", "true" );// NON-NLS

    ProxySelector.setDefault( new FirewallingProxySelector( ProxySelector.getDefault() ) );
    if ( offlineModeSecurityManager ) {
      try {
        System.setSecurityManager( new FirewallingSecurityManager() );
      } catch ( SecurityException se ) {
        DebugLog.log(
          "Unable to set security manager. An other security manager prevented us from gaining control." );// NON-NLS
      }
    }
    if ( offlineMode ) {
      WorkspaceSettings.getInstance().setOfflineMode( true );
    }

    PropertyEditorManager.registerEditor( Color.class, ColorPropertyEditor.class );

    try {
      SwingUtilities.invokeAndWait( new SetLookAndFeelTask() );
      SwingUtilities.invokeAndWait( new InstallAWTHandlerRunnable() );

      SwingUtilities.invokeAndWait( new InitializeSplashScreenTask() );
      // avoid the big cascading boot so that we can update the splashscreen
      // with some meaning full messages

      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Booting Base Libraries .." ) );// NON-NLS
      LibLoaderBoot.getInstance().start();
      if ( LibLoaderBoot.getInstance().isBootFailed() ) {
        throw new IllegalStateException( "Booting failed", LibLoaderBoot.getInstance().getBootFailureReason() );
      }
      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Booting Font Rendering System .." ) );// NON-NLS
      LibFontBoot.getInstance().start();
      if ( LibFontBoot.getInstance().isBootFailed() ) {
        throw new IllegalStateException( "Booting failed", LibFontBoot.getInstance().getBootFailureReason() );
      }
      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Booting Reporting-Engine .." ) );// NON-NLS
      ClassicEngineBoot.getInstance().start();
      if ( ClassicEngineBoot.getInstance().isBootFailed() ) {
        throw new IllegalStateException( "Booting failed", ClassicEngineBoot.getInstance().getBootFailureReason() );
      }
      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Booting Report-Designer .." ) );// NON-NLS
      ReportDesignerBoot.getInstance().start();
      if ( ReportDesignerBoot.getInstance().isBootFailed() ) {
        throw new IllegalStateException( "Booting failed", ReportDesignerBoot.getInstance().getBootFailureReason() );
      }

      // initialize some of the more expensive model components.
      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Preloading classes .." ) );// NON-NLS
      ExpressionRegistry.getInstance();
      ExpressionsTreeModel.getTreeModel();
      ExpressionUtil.getInstance();
      preloadFonts();

      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Checking initial configuration .." ) );// NON-NLS
      SettingsUtil.createInitialConfiguration();

      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Collecting Sample Reports .." ) );// NON-NLS
      SamplesTreeBuilder.getSampleTreeModel();

      SwingUtilities.invokeAndWait( new UpdateStatusTask( "Starting  .." ) );// NON-NLS
      SwingUtilities.invokeAndWait( new CreateReportDesignerFrame( files ) );

      final ElementMetaData data = ElementTypeRegistry.getInstance().getElementType( "page-header" );// NON-NLS
      final AttributeMetaData[] datas = data.getAttributeDescriptions();
      final int x = datas.length; // ensure that there is some metadata.
    } catch ( Throwable t ) {
      if ( splashScreen != null ) {
        splashScreen.dispose();
      }
      UncaughtExceptionsModel.getInstance().addException( t );
      final ExceptionDialog dialog = new ExceptionDialog();
      dialog.setModal( true );
      dialog.showDialog();
      System.exit( 1 );
    }
  }

  public static void preloadFonts() {
    final BufferedImage image = ImageUtils.createTransparentImage( 10, 10 );
    final Graphics2D d = image.createGraphics();
    d.setPaint( Color.BLACK );
    final String[] availableFontFamilyNames =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    for ( int i = 0; i < availableFontFamilyNames.length; i++ ) {
      final String value = availableFontFamilyNames[ i ];
      d.setFont( StyleContext.getDefaultStyleContext().getFont( value, Font.PLAIN, 12 ) );
      d.drawString( value, 0, 10 );
    }
    d.dispose();
  }

}
