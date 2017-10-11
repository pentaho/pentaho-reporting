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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * A component, that accepts a drawable and which draws that drawable.
 *
 * @author Thomas Morgner
 */
public class DrawablePanel extends JPanel {
  private DrawableWrapper drawable;

  public DrawablePanel() {
    setOpaque( false );
  }

  public DrawableWrapper getDrawable() {
    return drawable;
  }

  public void setDrawableAsRawObject( final Object o ) {
    if ( o == null ) {
      setDrawable( null );
    } else if ( o instanceof DrawableWrapper ) {
      setDrawable( (DrawableWrapper) o );
    } else {
      setDrawable( new DrawableWrapper( o ) );
    }
  }

  public void setDrawable( final DrawableWrapper drawable ) {
    this.drawable = drawable;
    revalidate();
    repaint();
  }

  /**
   * If the <code>preferredSize</code> has been set to a non-<code>null</code> value just returns it. If the UI
   * delegate's <code>getPreferredSize</code> method returns a non <code>null</code> value then return that; otherwise
   * defer to the component's layout manager.
   *
   * @return the value of the <code>preferredSize</code> property
   * @see #setPreferredSize
   * @see javax.swing.plaf.ComponentUI
   */
  public Dimension getPreferredSize() {
    if ( drawable == null ) {
      return new Dimension( 0, 0 );
    }
    return drawable.getPreferredSize();
  }

  /**
   * If the minimum size has been set to a non-<code>null</code> value just returns it. If the UI delegate's
   * <code>getMinimumSize</code> method returns a non-<code>null</code> value then return that; otherwise defer to the
   * component's layout manager.
   *
   * @return the value of the <code>minimumSize</code> property
   * @see #setMinimumSize
   * @see javax.swing.plaf.ComponentUI
   */
  public Dimension getMinimumSize() {
    if ( drawable == null ) {
      return new Dimension( 0, 0 );
    }
    return drawable.getPreferredSize();
  }

  /**
   * Returns true if this component is completely opaque.
   * <p/>
   * An opaque component paints every pixel within its rectangular bounds. A non-opaque component paints only a subset
   * of its pixels or none at all, allowing the pixels underneath it to "show through". Therefore, a component that does
   * not fully paint its pixels provides a degree of transparency.
   * <p/>
   * Subclasses that guarantee to always completely paint their contents should override this method and return true.
   *
   * @return true if this component is completely opaque
   * @see #setOpaque
   */
  public boolean isOpaque() {
    if ( drawable == null ) {
      return false;
    }
    return super.isOpaque();
  }

  /**
   * Calls the UI delegate's paint method, if the UI delegate is non-<code>null</code>. We pass the delegate a copy of
   * the <code>Graphics</code> object to protect the rest of the paint code from irrevocable changes (for example,
   * <code>Graphics.translate</code>).
   * <p/>
   * If you override this in a subclass you should not make permanent changes to the passed in <code>Graphics</code>.
   * For example, you should not alter the clip <code>Rectangle</code> or modify the transform. If you need to do these
   * operations you may find it easier to create a new <code>Graphics</code> from the passed in <code>Graphics</code>
   * and manipulate it. Further, if you do not invoker super's implementation you must honor the opaque property, that
   * is if this component is opaque, you must completely fill in the background in a non-opaque color. If you do not
   * honor the opaque property you will likely see visual artifacts.
   * <p/>
   * The passed in <code>Graphics</code> object might have a transform other than the identify transform installed on
   * it. In this case, you might get unexpected results if you cumulatively apply another transform.
   *
   * @param g
   *          the <code>Graphics</code> object to protect
   * @see #paint
   * @see javax.swing.plaf.ComponentUI
   */
  protected void paintComponent( final Graphics g ) {
    super.paintComponent( g );
    if ( drawable == null ) {
      return;
    }

    final Graphics2D g2 = (Graphics2D) g.create( 0, 0, getWidth(), getHeight() );

    drawable.draw( g2, new Rectangle2D.Double( 0, 0, getWidth(), getHeight() ) );
    g2.dispose();
  }
}
