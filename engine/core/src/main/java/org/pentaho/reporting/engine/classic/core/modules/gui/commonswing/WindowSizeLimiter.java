/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
