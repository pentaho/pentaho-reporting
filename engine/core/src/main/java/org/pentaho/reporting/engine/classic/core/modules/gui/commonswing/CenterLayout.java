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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

/**
 * A layout manager that displays a single component in the center of its container.
 *
 * @author David Gilbert
 */
public class CenterLayout implements LayoutManager, Serializable {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 469319532333015042L;

  /**
   * Creates a new layout manager.
   */
  public CenterLayout() {
  }

  /**
   * Returns the preferred size.
   *
   * @param parent
   *          the parent.
   * @return the preferred size.
   */
  public Dimension preferredLayoutSize( final Container parent ) {

    synchronized ( parent.getTreeLock() ) {
      final Insets insets = parent.getInsets();
      if ( parent.getComponentCount() > 0 ) {
        final Component component = parent.getComponent( 0 );
        final Dimension d = component.getPreferredSize();
        return new Dimension( (int) d.getWidth() + insets.left + insets.right, (int) d.getHeight() + insets.top
            + insets.bottom );
      } else {
        return new Dimension( insets.left + insets.right, insets.top + insets.bottom );
      }
    }

  }

  /**
   * Returns the minimum size.
   *
   * @param parent
   *          the parent.
   * @return the minimum size.
   */
  public Dimension minimumLayoutSize( final Container parent ) {

    synchronized ( parent.getTreeLock() ) {
      final Insets insets = parent.getInsets();
      if ( parent.getComponentCount() > 0 ) {
        final Component component = parent.getComponent( 0 );
        final Dimension d = component.getMinimumSize();
        return new Dimension( d.width + insets.left + insets.right, d.height + insets.top + insets.bottom );
      } else {
        return new Dimension( insets.left + insets.right, insets.top + insets.bottom );
      }
    }

  }

  /**
   * Lays out the components.
   *
   * @param parent
   *          the parent.
   */
  public void layoutContainer( final Container parent ) {

    synchronized ( parent.getTreeLock() ) {
      if ( parent.getComponentCount() > 0 ) {
        final Insets insets = parent.getInsets();
        final Dimension parentSize = parent.getSize();
        final Component component = parent.getComponent( 0 );
        final Dimension componentSize = component.getPreferredSize();
        final int xx =
            insets.left + ( Math.max( ( parentSize.width - insets.left - insets.right - componentSize.width ) / 2, 0 ) );
        final int yy =
            insets.top
                + ( Math.max( ( parentSize.height - insets.top - insets.bottom - componentSize.height ) / 2, 0 ) );
        component.setBounds( xx, yy, componentSize.width, componentSize.height );
      }
    }

  }

  /**
   * Not used.
   *
   * @param comp
   *          the component.
   */
  public void addLayoutComponent( final Component comp ) {
    // not used.
  }

  /**
   * Not used.
   *
   * @param comp
   *          the component.
   */
  public void removeLayoutComponent( final Component comp ) {
    // not used
  }

  /**
   * Not used.
   *
   * @param name
   *          the component name.
   * @param comp
   *          the component.
   */
  public void addLayoutComponent( final String name, final Component comp ) {
    // not used
  }

  /**
   * Not used.
   *
   * @param name
   *          the component name.
   * @param comp
   *          the component.
   */
  public void removeLayoutComponent( final String name, final Component comp ) {
    // not used
  }

}
