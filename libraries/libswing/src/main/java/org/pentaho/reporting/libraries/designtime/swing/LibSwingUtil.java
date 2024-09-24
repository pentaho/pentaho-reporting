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

package org.pentaho.reporting.libraries.designtime.swing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.util.StringTokenizer;

/**
 * Utility classes for swing. This is an exact copy of SwingUtil found in the Engine Core ... but this project does not
 * depend on that one, and therefore can not use it. Likewise, that project does not depend on this one and can not use
 * it.
 */
public class LibSwingUtil {
  private static final Log logger = LogFactory.getLog( LibSwingUtil.class );

  private LibSwingUtil() {
  }

  /**
   * Positions the specified frame in the middle of the screen.
   *
   * @param frame the frame to be centered on the screen.
   */
  public static void centerFrameOnScreen( final Window frame ) {
    positionFrameOnScreen( frame, 0.5, 0.5 );
  }

  /**
   * Positions the specified frame at a relative position in the screen, where 50% is considered to be the center of the
   * screen.
   *
   * @param frame             the frame.
   * @param horizontalPercent the relative horizontal position of the frame (0.0 to 1.0, where 0.5 is the center of the
   *                          screen).
   * @param verticalPercent   the relative vertical position of the frame (0.0 to 1.0, where 0.5 is the center of the
   *                          screen).
   */
  public static void positionFrameOnScreen( final Window frame,
                                            final double horizontalPercent,
                                            final double verticalPercent ) {

    final Rectangle s = frame.getGraphicsConfiguration().getBounds();
    final Dimension f = frame.getSize();

    final int spaceOnX = Math.max( s.width - f.width, 0 );
    final int spaceOnY = Math.max( s.height - f.height, 0 );
    final int x = (int) ( horizontalPercent * spaceOnX ) + s.x;
    final int y = (int) ( verticalPercent * spaceOnY ) + s.y;
    frame.setBounds( x, y, f.width, f.height );
    frame.setBounds( s.intersection( frame.getBounds() ) );
  }

  /**
   * Positions the specified frame at a random location on the screen while ensuring that the entire frame is visible
   * (provided that the frame is smaller than the screen).
   *
   * @param frame the frame.
   */
  public static void positionFrameRandomly( final Window frame ) {
    //noinspection UnsecureRandomNumberGeneration
    positionFrameOnScreen( frame, Math.random(), Math.random() );
  }

  /**
   * Positions the specified dialog within its parent.
   *
   * @param dialog the dialog to be positioned on the screen.
   */
  public static void centerDialogInParent( final Dialog dialog ) {
    positionDialogRelativeToParent( dialog, 0.5, 0.5 );
  }

  /**
   * Positions the specified dialog at a position relative to its parent.
   *
   * @param dialog            the dialog to be positioned.
   * @param horizontalPercent the relative location.
   * @param verticalPercent   the relative location.
   */
  public static void positionDialogRelativeToParent( final Dialog dialog,
                                                     final double horizontalPercent,
                                                     final double verticalPercent ) {
    final Container parent = dialog.getParent();
    if ( parent == null || ( parent.isVisible() == false ) ) {
      positionFrameOnScreen( dialog, horizontalPercent, verticalPercent );
      return;
    }

    final Dimension d = dialog.getSize();
    final Dimension p = parent.getSize();

    final int baseX = parent.getX();
    final int baseY = parent.getY();

    final int parentPointX = baseX + (int) ( horizontalPercent * p.width );
    final int parentPointY = baseY + (int) ( verticalPercent * p.height );

    final int dialogPointX = parentPointX - (int) ( horizontalPercent * d.width );
    final int dialogPointY = parentPointY - (int) ( verticalPercent * d.height );

    // make sure the dialog fits completely on the screen...
    final Rectangle s = parent.getGraphicsConfiguration().getBounds();
    final Rectangle r = new Rectangle( dialogPointX, dialogPointY, d.width, d.height );
    final Rectangle intersectedDialogBounds = r.intersection( s );
    if ( intersectedDialogBounds.width < d.width ) {
      r.x = s.width - d.width;
      r.width = d.width;
    }
    if ( intersectedDialogBounds.height < d.height ) {
      r.y = s.height - d.height;
      r.height = d.height;
    }
    final Rectangle finalIntersection = r.intersection( s );
    dialog.setBounds( finalIntersection );
  }


  public static Window getWindowAncestor( Component component ) {
    while ( component instanceof Window == false ) {
      if ( component == null ) {
        return null;
      }
      component = component.getParent();
    }
    return (Window) component;
  }

  public static boolean safeRestoreWindow( final Window frame, final Rectangle bounds ) {
    final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final GraphicsDevice[] devices = graphicsEnvironment.getScreenDevices();
    for ( int i = 0; i < devices.length; i++ ) {
      final GraphicsDevice device = devices[ i ];
      final Rectangle rectangle = device.getDefaultConfiguration().getBounds();
      if ( rectangle.contains( bounds ) || rectangle.equals( bounds ) ) {
        logger.info( "Found a usable screen-configuration: Restoring frame to " + bounds );
        frame.setBounds( bounds );
        return true;
      }
    }
    return false;
  }

  public static String rectangleToString( final Rectangle rectangle ) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append( rectangle.getX() );
    buffer.append( "," );
    buffer.append( rectangle.getY() );
    buffer.append( "," );
    buffer.append( rectangle.getWidth() );
    buffer.append( "," );
    buffer.append( rectangle.getHeight() );
    return buffer.toString();
  }

  public static Rectangle parseRectangle( final String boundsAsText ) {
    try {
      final StringTokenizer tokenizer = new StringTokenizer( boundsAsText, "," );
      if ( tokenizer.countTokens() == 4 ) {
        final double x = Double.parseDouble( tokenizer.nextToken() );
        final double y = Double.parseDouble( tokenizer.nextToken() );
        final double width = Double.parseDouble( tokenizer.nextToken() );
        final double height = Double.parseDouble( tokenizer.nextToken() );

        final Rectangle rectangle = new Rectangle();
        rectangle.setRect( x, y, width, height );
        return rectangle;
      }
      return null;
    } catch ( Exception e ) {
      logger.warn( "Error while getting initial frame bounds.", e ); // NON-NLS
      return null;
    }
  }


}
