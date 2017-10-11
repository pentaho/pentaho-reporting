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

package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import javax.swing.*;
import java.awt.*;

public class ColorPreviewPanel extends JComponent {
  private Color previous;
  private Color current;

  public ColorPreviewPanel() {
    previous = Color.white;
    current = Color.white;
  }

  public Color getPrevious() {
    return previous;
  }

  public void setPrevious( final Color previous ) {
    if ( previous == null ) {
      throw new NullPointerException();
    }
    this.previous = previous;
    repaint();
  }

  public Color getCurrent() {
    return current;
  }

  public void setCurrent( final Color current ) {
    if ( current == null ) {
      throw new NullPointerException();
    }
    this.current = current;
    repaint();
  }

  public Dimension getPreferredSize() {
    return new Dimension( 60, 60 );
  }

  public Dimension getMinimumSize() {
    return new Dimension( 60, 60 );
  }

  protected void paintComponent( final Graphics g ) {
    final Graphics graphics = g.create();
    graphics.setColor( current );
    graphics.fillRect( 0, 0, getWidth(), getHeight() / 2 );
    graphics.setColor( previous );
    graphics.fillRect( 0, getHeight() / 2, getWidth(), getHeight() / 2 );
    graphics.dispose();
  }
}
