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

package org.pentaho.reporting.designer.core.widgets;

import javax.swing.*;
import java.awt.*;

/**
 * The class which generates the 'X' icon for the tabs. The constructor accepts an icon which is extra to the 'X' icon,
 * so you can have tabs like in JBuilder. This value is null if no extra icon is required.
 */
public class CloseTabIcon implements Icon {
  /**
   * the width the icon
   */
  private int width;

  /**
   * the height the icon
   */
  private int height;

  /**
   * true whether the mouse is over this icon, false otherwise
   */
  private boolean mouseover;

  /**
   * true whether the mouse is pressed on this icon, false otherwise
   */
  private boolean mousepressed;

  /**
   * Creates a new instance of <code>CloseTabIcon</code>
   */
  public CloseTabIcon( final boolean mouseOverIcon, final boolean mousePressed ) {
    this.mouseover = mouseOverIcon;
    this.mousepressed = mousePressed;
    width = 16;
    height = 16;
  }

  /**
   * Draw the icon at the specified location. Icon implementations may use the Component argument to get properties
   * useful for painting, e.g. the foreground or background color.
   *
   * @param c    the component which the icon belongs to
   * @param gorg the graphic object to draw on
   * @param x    the upper left point of the icon in the x direction
   * @param y    the upper left point of the icon in the y direction
   */
  public void paintIcon( final Component c, final Graphics gorg, final int x, final int y ) {
    final Graphics2D g = (Graphics2D) gorg.create();
    if ( !mousepressed ) {
      g.translate( -1, -1 );
    }

    if ( mousepressed && mouseover ) {
      g.setColor( Color.WHITE );
      g.fillRect( x + 1, y + 1, 14, 14 );
    }

    g.setColor( Color.black );
    g.drawRect( x + 1, y + 1, 14, 14 );
    if ( mouseover ) {
      g.setColor( Color.GRAY );
    }

    g.setStroke( new BasicStroke( 2 ) );
    // from top left to bottom right
    g.drawLine( x + 5, y + 5, x + 12, y + 12 );
    // from bottom left to top right
    g.drawLine( x + 12, y + 5, x + 5, y + 12 );

    g.dispose();
  }

  /**
   * Returns the icon's width.
   *
   * @return an int specifying the fixed width of the icon.
   */
  public int getIconWidth() {
    return width;
  }

  /**
   * Returns the icon's height.
   *
   * @return an int specifying the fixed height of the icon.
   */
  public int getIconHeight() {
    return height;
  }

}
