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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Creation-Date: 20.11.2006, 22:42:21
 *
 * @author Thomas Morgner
 */
public class SwingUtil {
  private SwingUtil() {
  }

  /**
   * Computes the maximum bounds of the current screen device. If this method is called on JDK 1.4, Xinerama-aware
   * results are returned. (See Sun-Bug-ID 4463949 for details).
   *
   * @return the maximum bounds of the current screen.
   */
  public static Rectangle getMaximumWindowBounds() {
    try {
      final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return localGraphicsEnvironment.getMaximumWindowBounds();
    } catch ( Exception e ) {
      // ignore ... will fail if this is not a JDK 1.4 ..
    }

    final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
    return new Rectangle( 0, 0, s.width, s.height );
  }

  /**
   * Positions the specified frame in the middle of the screen.
   *
   * @param frame
   *          the frame to be centered on the screen.
   */
  public static void centerFrameOnScreen( final Window frame ) {
    positionFrameOnScreen( frame, 0.5, 0.5 );
  }

  /**
   * Positions the specified frame at a relative position in the screen, where 50% is considered to be the center of the
   * screen.
   *
   * @param frame
   *          the frame.
   * @param horizontalPercent
   *          the relative horizontal position of the frame (0.0 to 1.0, where 0.5 is the center of the screen).
   * @param verticalPercent
   *          the relative vertical position of the frame (0.0 to 1.0, where 0.5 is the center of the screen).
   */
  public static void positionFrameOnScreen( final Window frame, final double horizontalPercent,
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
   * @param frame
   *          the frame.
   */
  public static void positionFrameRandomly( final Window frame ) {
    positionFrameOnScreen( frame, Math.random(), Math.random() );
  }

  /**
   * Positions the specified dialog within its parent.
   *
   * @param dialog
   *          the dialog to be positioned on the screen.
   */
  public static void centerDialogInParent( final Dialog dialog ) {
    positionDialogRelativeToParent( dialog, 0.5, 0.5 );
  }

  /**
   * Positions the specified dialog at a position relative to its parent.
   *
   * @param dialog
   *          the dialog to be positioned.
   * @param horizontalPercent
   *          the relative location.
   * @param verticalPercent
   *          the relative location.
   */
  public static void positionDialogRelativeToParent( final Dialog dialog, final double horizontalPercent,
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

    final int dialogPointX = Math.max( 0, parentPointX - (int) ( horizontalPercent * d.width ) );
    final int dialogPointY = Math.max( 0, parentPointY - (int) ( verticalPercent * d.height ) );

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
}
