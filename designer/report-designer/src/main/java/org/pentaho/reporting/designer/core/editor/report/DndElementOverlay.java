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

package org.pentaho.reporting.designer.core.editor.report;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.font.LineMetrics;

/**
 * This is the graphical representation of a element that is currently dragged into a editor-pane.
 *
 * @author Thomas Morgner
 */
public class DndElementOverlay extends JLabel {
  private float zoom;

  public DndElementOverlay() {
    setDoubleBuffered( true );
    setOpaque( false );
    zoom = 1;
  }

  public float getZoom() {
    return zoom;
  }

  public void setZoom( final float zoom ) {
    this.zoom = zoom;
  }

  /**
   * If the <code>preferredSize</code> has been set to a non-<code>null</code> value just returns it. If the UI
   * delegate's <code>getPreferredSize</code> method returns a non <code>null</code> value then return that; otherwise
   * defer to the component's layout manager.
   *
   * @return the value of the <code>preferredSize</code> property
   * @see #setPreferredSize
   * @see ComponentUI
   */
  public Dimension getPreferredSize() {
    final Dimension preferredSize = super.getPreferredSize();
    preferredSize
      .setSize( Math.ceil( preferredSize.getWidth() * zoom ), Math.ceil( preferredSize.getHeight() * zoom ) );
    return preferredSize;
  }

  /**
   * If the maximum size has been set to a non-<code>null</code> value just returns it.  If the UI delegate's
   * <code>getMaximumSize</code> method returns a non-<code>null</code> value then return that; otherwise defer to the
   * component's layout manager.
   *
   * @return the value of the <code>maximumSize</code> property
   * @see #setMaximumSize
   * @see ComponentUI
   */
  public Dimension getMaximumSize() {
    final Dimension preferredSize = super.getMaximumSize();
    preferredSize
      .setSize( Math.ceil( preferredSize.getWidth() * zoom ), Math.ceil( preferredSize.getHeight() * zoom ) );
    return preferredSize;
  }

  /**
   * If the minimum size has been set to a non-<code>null</code> value just returns it.  If the UI delegate's
   * <code>getMinimumSize</code> method returns a non-<code>null</code> value then return that; otherwise defer to the
   * component's layout manager.
   *
   * @return the value of the <code>minimumSize</code> property
   * @see #setMinimumSize
   * @see ComponentUI
   */
  public Dimension getMinimumSize() {
    final Dimension preferredSize = super.getMinimumSize();
    preferredSize
      .setSize( Math.ceil( preferredSize.getWidth() * zoom ), Math.ceil( preferredSize.getHeight() * zoom ) );
    return preferredSize;
  }

  /**
   * Invoked by Swing to draw components. Applications should not invoke <code>paint</code> directly, but should instead
   * use the <code>repaint</code> method to schedule the component for redrawing.
   * <p/>
   * This method actually delegates the work of painting to three protected methods: <code>paintComponent</code>,
   * <code>paintBorder</code>, and <code>paintChildren</code>.  They're called in the order listed to ensure that
   * children appear on top of component itself. Generally speaking, the component and its children should not paint in
   * the insets area allocated to the border. Subclasses can just override this method, as always.  A subclass that just
   * wants to specialize the UI (look and feel) delegate's <code>paint</code> method should just override
   * <code>paintComponent</code>.
   *
   * @param g the <code>Graphics</code> context in which to paint
   * @see #paintComponent
   * @see #paintBorder
   * @see #paintChildren
   * @see #getComponentGraphics
   * @see #repaint
   */
  public void paintComponent( final Graphics g ) {
    final Graphics2D g2 = (Graphics2D) g;
    g2.scale( zoom, zoom );
    g2.setColor( getBackground() );
    g2.fillRect( 0, 0, getWidth(), getHeight() );

    g2.setColor( getForeground() );
    final Icon icon = getIcon();
    final int gap = getIconTextGap();
    final String text = getText();
    final Insets insets = getInsets();
    if ( icon != null && text != null ) {
      icon.paintIcon( this, g2, insets.left, insets.top );
      g2.setFont( getFont() );
      final FontMetrics fontMetrics = g2.getFontMetrics();
      final LineMetrics lineMetrics = fontMetrics.getLineMetrics( text, g2 );
      final float baseLine = lineMetrics.getAscent();
      final float iconWidth = icon.getIconWidth();
      final float textX = insets.left + gap + iconWidth;
      final float iconHeight = icon.getIconHeight();
      final float textY = insets.top + baseLine + ( iconHeight - lineMetrics.getHeight() );
      g2.drawString( text, textX, textY );
    } else if ( icon != null ) {
      icon.paintIcon( this, g2, insets.left, insets.top );
    } else if ( text != null ) {
      g2.setFont( getFont() );
      final FontMetrics fontMetrics = g2.getFontMetrics();
      final LineMetrics lineMetrics = fontMetrics.getLineMetrics( text, g2 );
      final float baseLine = lineMetrics.getAscent();
      final int textX = insets.left;
      final float textY = insets.top + baseLine;
      g2.drawString( text, textX, textY );
    }

    final Border border = getBorder();
    if ( border != null ) {
      border.paintBorder( this, g2, 0, 0, (int) ( getWidth() / zoom ), (int) ( getHeight() / zoom ) );
    }
    g2.dispose();
  }

}
