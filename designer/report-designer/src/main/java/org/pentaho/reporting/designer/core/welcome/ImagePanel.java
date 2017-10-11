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

package org.pentaho.reporting.designer.core.welcome;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class ImagePanel extends JPanel {
  private boolean scaleX;
  private boolean scaleY;
  private Image image;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public ImagePanel( final Image image, final boolean scaleX, final boolean scaleY ) {
    this.image = image;
    this.scaleX = scaleX;
    this.scaleY = scaleY;
  }

  public void paintComponent( final Graphics g ) {
    super.paintComponent( g );
    if ( image == null ) {
      return;
    }

    if ( scaleX && scaleY ) {
      g.drawImage( image, 0, 0, getWidth(), getHeight(), this );
    } else if ( scaleX ) {
      g.drawImage( image, 0, 0, getWidth(), image.getHeight( this ), this );
    } else if ( scaleY ) {
      g.drawImage( image, 0, 0, image.getWidth( this ), getHeight(), this );
    } else {
      g.drawImage( image, 0, 0, image.getWidth( this ), image.getHeight( this ), this );
    }
  }
}
