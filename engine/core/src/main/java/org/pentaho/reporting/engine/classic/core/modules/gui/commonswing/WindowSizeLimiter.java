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
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A small helper class to limit the maximum size of an element to the elements maximum size. If the element is resized,
 * the defined maximum size is enforced on the element.
 *
 * @author Thomas Morgner
 */
public class WindowSizeLimiter extends ComponentAdapter {
  /**
   * The current source.
   */
  private Object currentSource;

  /**
   * Default-Constructor.
   */
  public WindowSizeLimiter() {

  }

  /**
   * Invoked when the component's size changes.
   *
   * @param e
   *          the event.
   */
  public void componentResized( final ComponentEvent e ) {
    if ( e.getSource() == currentSource ) {
      return;
    }

    if ( e.getSource() instanceof Component ) {
      currentSource = e.getSource();
      final Component c = (Component) e.getSource();
      final Dimension d = c.getMaximumSize();
      final Dimension s = c.getSize();
      if ( s.width > d.width ) {
        s.width = d.width;
      }
      if ( s.height > d.height ) {
        s.height = d.height;
      }
      c.setSize( s );
      currentSource = null;
    }

  }
}
