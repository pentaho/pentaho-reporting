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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.PngEncoder;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class ScreenCaptureAction extends AbstractDesignerContextAction {
  private static class ScreenShotFilenameFilter implements FilenameFilter {

    private ScreenShotFilenameFilter() {
    }

    public boolean accept( final File dir, final String name ) {
      if ( name.startsWith( PREFIX ) && name.endsWith( PNG_SUFFIX ) ) {
        return true;
      }
      return false;
    }
  }

  private static class GlobalKeyEventHandler implements KeyEventPostProcessor {
    public boolean postProcessKeyEvent( final KeyEvent e ) {
      if ( e.getID() == KeyEvent.KEY_PRESSED ) {
        final int menuKeyMask = getMenuKeyMask();
        if ( e.getKeyCode() == KeyEvent.VK_P &&
          ( e.getModifiers() & menuKeyMask ) == menuKeyMask ) {
          saveScreenShot( e.getModifiers() );
          return true;
        }
      }
      return false;
    }
  }

  private static final Log logger = LogFactory.getLog( ScreenCaptureAction.class );
  private static final String PNG_SUFFIX = ".png";
  private static final String PREFIX = "prd-screen-capture-";
  private static boolean installed;

  public ScreenCaptureAction() {
    putValue( Action.NAME, ActionMessages.getString( "ScreenCaptureAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ScreenCaptureAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ScreenCaptureAction.Mnemonic" ) );
    //putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("ScreenCaptureAction.Accelerator"));
    installGlobally();
  }

  public void actionPerformed( final ActionEvent e ) {
    saveScreenShot( e.getModifiers() );
  }

  public static void installGlobally() {
    if ( !installed ) {
      installed = true;
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor( new GlobalKeyEventHandler() );
    }
  }

  private static int getMenuKeyMask() {
    try {
      return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    } catch ( UnsupportedOperationException he ) {
      // headless exception extends UnsupportedOperation exception,
      // but the HeadlessException is not defined in older JDKs...
      return InputEvent.CTRL_MASK;
    }
  }

  public static void saveScreenShot( final int modifiers ) {
    final Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    final GraphicsConfiguration graphicsConfiguration = component.getGraphicsConfiguration();
    final GraphicsDevice graphicsDevice = graphicsConfiguration.getDevice();
    try {
      final Robot robot = new Robot( graphicsDevice );
      final BufferedImage image;
      if ( ( modifiers & ActionEvent.SHIFT_MASK ) == ActionEvent.SHIFT_MASK ) {
        image = robot.createScreenCapture( graphicsConfiguration.getBounds() );
      } else {
        image = robot.createScreenCapture( component.getBounds() );
      }

      final String homeDirectory =
        ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty( "user.home", "." );
      final File homeDir = new File( homeDirectory );
      final File f = generateName( homeDir );
      if ( f == null ) {
        return;
      }
      final FileOutputStream fout = new FileOutputStream( f );
      try {
        final PngEncoder encoder = new PngEncoder();
        encoder.setCompressionLevel( 6 );
        encoder.setEncodeAlpha( false );
        encoder.setImage( image );
        final byte[] bytes = encoder.pngEncode();
        fout.write( bytes );
      } finally {
        fout.close();
      }
    } catch ( IOException ioe ) {
      UncaughtExceptionsModel.getInstance().addException( ioe );
    } catch ( AWTException e1 ) {
      // ignore
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }

  private static File generateName( final File directory ) {
    final File[] files = directory.listFiles( new ScreenShotFilenameFilter() );
    int max = 0;
    for ( int i = 0; i < files.length; i++ ) {
      try {
        final File file = files[ i ];
        final String name = file.getName().substring( PREFIX.length() ).substring( PNG_SUFFIX.length() );
        final int idx = ParserUtil.parseInt( name, -1 );
        if ( idx > max ) {
          max = idx;
        }
      } catch ( Exception e ) {
        // non-fatal, complete ignore
        logger.debug( e );
      }
    }

    for ( int i = max + 1; i < 99999; i++ ) {
      final String nodeName = PREFIX + i + PNG_SUFFIX;
      final File f = new File( directory, nodeName );
      if ( f.exists() == false ) {
        return f;
      }
    }
    return null;
  }
}
