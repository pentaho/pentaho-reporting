/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.widgets;

import org.pentaho.reporting.designer.core.util.CanvasImageLoader;

import javax.swing.*;
import java.awt.*;

public class FancyTabbedPane extends JTabbedPane {
  /**
   * Creates an empty <code>TabbedPane</code> with a default tab placement of <code>JTabbedPane.TOP</code>.
   *
   * @see #addTab
   */
  public FancyTabbedPane() {
  }

  protected void paintComponent( final Graphics g ) {
    super.paintComponent( g );
    if ( getTabCount() == 0 ) {
      final Image img = CanvasImageLoader.getInstance().getBackgroundImage().getImage();
      g.drawImage( img, 0, 0, this );
    }
  }
}
