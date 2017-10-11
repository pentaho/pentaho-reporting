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

package org.pentaho.reporting.engine.classic.core.testsupport.graphics;

import org.pentaho.reporting.libraries.base.util.DebugLog;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class TestGraphics2D extends AbstractGraphics2D {
  public TestGraphics2D() {
    this( 2, 2 );
  }

  public TestGraphics2D( final int width, final int height ) {
    super( width, height );
  }

  protected boolean drawImage( final Image img, final Image mask, final AffineTransform xform, final Color bgColor,
      final ImageObserver obs ) {
    return false;
  }

  /**
   * Strokes the outline of a <code>Shape</code> using the settings of the current <code>Graphics2D</code> context. The
   * rendering attributes applied include the <code>Clip</code>, <code>Transform</code>, <code>Paint</code>,
   * <code>Composite</code> and <code>Stroke</code> attributes.
   *
   * @param s
   *          the <code>Shape</code> to be rendered
   * @see #setStroke
   * @see #setPaint
   * @see java.awt.Graphics#setColor
   * @see #transform
   * @see #setTransform
   * @see #clip
   * @see #setClip
   * @see #setComposite
   */
  public void draw( final Shape s ) {

  }

  /**
   * Renders the text specified by the specified <code>String</code>, using the current text attribute state in the
   * <code>Graphics2D</code> context. The baseline of the first character is at position (<i>x</i>,&nbsp;<i>y</i>) in
   * the User Space. The rendering attributes applied include the <code>Clip</code>, <code>Transform</code>,
   * <code>Paint</code>, <code>Font</code> and <code>Composite</code> attributes. For characters in script systems such
   * as Hebrew and Arabic, the glyphs can be rendered from right to left, in which case the coordinate supplied is the
   * location of the leftmost character on the baseline.
   *
   * @param str
   *          the <code>String</code> to be rendered
   * @param x
   *          the x coordinate of the location where the <code>String</code> should be rendered
   * @param y
   *          the y coordinate of the location where the <code>String</code> should be rendered
   * @throws NullPointerException
   *           if <code>str</code> is <code>null</code>
   * @see #setPaint
   * @see java.awt.Graphics#setColor
   * @see java.awt.Graphics#setFont
   * @see #setTransform
   * @see #setComposite
   * @see #setClip
   */
  public void drawString( final String str, final float x, final float y ) {
    DebugLog.log( "drawString(..): " + x + ", " + y + " = " + str );
    DebugLog.log( "drawString(..): CLIP=" + getClipBounds() );
  }

  /**
   * Renders the text of the specified iterator applying its attributes in accordance with the specification of the
   * {@link java.awt.font.TextAttribute} class.
   * <p/>
   * The baseline of the first character is at position (<i>x</i>,&nbsp;<i>y</i>) in User Space. For characters in
   * script systems such as Hebrew and Arabic, the glyphs can be rendered from right to left, in which case the
   * coordinate supplied is the location of the leftmost character on the baseline.
   *
   * @param iterator
   *          the iterator whose text is to be rendered
   * @param x
   *          the x coordinate where the iterator's text is to be rendered
   * @param y
   *          the y coordinate where the iterator's text is to be rendered
   * @throws NullPointerException
   *           if <code>iterator</code> is <code>null</code>
   * @see #setPaint
   * @see java.awt.Graphics#setColor
   * @see #setTransform
   * @see #setComposite
   * @see #setClip
   */
  public void drawString( final AttributedCharacterIterator iterator, final float x, final float y ) {
    DebugLog.log( "drawString(..): " + iterator.toString() );
  }

  /**
   * Fills the interior of a <code>Shape</code> using the settings of the <code>Graphics2D</code> context. The rendering
   * attributes applied include the <code>Clip</code>, <code>Transform</code>, <code>Paint</code>, and
   * <code>Composite</code>.
   *
   * @param s
   *          the <code>Shape</code> to be filled
   * @see #setPaint
   * @see java.awt.Graphics#setColor
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   */
  public void fill( final Shape s ) {

  }
}
