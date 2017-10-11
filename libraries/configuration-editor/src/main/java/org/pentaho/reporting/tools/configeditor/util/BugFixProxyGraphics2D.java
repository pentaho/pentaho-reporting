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

package org.pentaho.reporting.tools.configeditor.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * This documents another sad chapter in Sun's java implementation. The tree cell renderer does not check the clipping
 * of the painted graphics correctly.
 * <p/>
 * This bug seems to be related to the TreeCellRenderer's unability to display the defined (and drawn) background of the
 * CellRenderers render component.
 * <p/>
 * A first hint of that bug can be found at <a href="http://www.cs.cf.ac.uk/Dave/HCI/HCI_Handout_CALLER/node155.html">
 * http://www.cs.cf.ac.uk/Dave/HCI/HCI_Handout_CALLER/node155.html</a>
 *
 * @author Thomas Morgner
 */
public class BugFixProxyGraphics2D extends Graphics2D {
  /**
   * The proxy parent.
   */
  private final Graphics2D parent;

  /**
   * Creates a new proxy to fix some of the Swing bugs.
   *
   * @param parent the original graphics object
   */
  public BugFixProxyGraphics2D( final Graphics2D parent ) {
    this.parent = parent;
  }

  /**
   * Forwards the call to the parent.
   *
   * @param s the <code>Shape</code> to be rendered
   */
  public void draw( final Shape s ) {
    parent.draw( s );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param img   the <code>Image</code> to be rendered
   * @param xform the transformation from image space into user space
   * @param obs   the {@link ImageObserver} to be notified as more of the <code>Image</code> is converted
   * @return <code>true</code> if the <code>Image</code> is fully loaded and completely rendered; <code>false</code> if
   * the <code>Image</code> is still being loaded.
   */
  public boolean drawImage( final Image img,
                            final AffineTransform xform,
                            final ImageObserver obs ) {
    return parent.drawImage( img, xform, obs );
  }

  /**
   * Renders a <code>BufferedImage</code> that is filtered with a {@link BufferedImageOp}. The rendering attributes
   * applied include the <code>Clip</code>, <code>Transform</code> and <code>Composite</code> attributes.  This is
   * equivalent to:
   * <pre>
   * img1 = op.filter(img, null);
   * drawImage(img1, new AffineTransform(1f,0f,0f,1f,x,y), null);
   * </pre>
   *
   * @param op  the filter to be applied to the image before rendering
   * @param img the <code>BufferedImage</code> to be rendered
   * @param x   the painting location ...
   * @param y   the location in user space where the upper left corner of the image is rendered
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   */
  public void drawImage( final BufferedImage img,
                         final BufferedImageOp op,
                         final int x,
                         final int y ) {
    parent.drawImage( img, op, x, y );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param img   the image to be rendered
   * @param xform the transformation from image space into user space
   */
  public void drawRenderedImage( final RenderedImage img,
                                 final AffineTransform xform ) {
    parent.drawRenderedImage( img, xform );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param img   the image to be rendered
   * @param xform the transformation from image space into user space
   */
  public void drawRenderableImage( final RenderableImage img,
                                   final AffineTransform xform ) {
    parent.drawRenderableImage( img, xform );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param str the string to be rendered
   * @param x   coordinates
   * @param y   the coordinates where the <code>String</code> should be rendered
   * @throws NullPointerException if <code>str</code> is <code>null</code>
   * @since JDK1.0
   */
  public void drawString( final String str, final int x, final int y ) {
    parent.drawString( str, x, y );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param s the <code>String</code> to be rendered
   * @param x coordinates.
   * @param y the coordinates where the <code>String</code> should be rendered
   * @throws NullPointerException if <code>str</code> is <code>null</code>
   */
  public void drawString( final String s, final float x, final float y ) {
    parent.drawString( s, x, y );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param iterator the iterator whose text is to be rendered
   * @param x        coords.
   * @param y        the coordinates where the iterator's text is to be rendered
   */
  public void drawString( final AttributedCharacterIterator iterator,
                          final int x, final int y ) {
    parent.drawString( iterator, x, y );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param iterator the iterator whose text is to be rendered
   * @param x        coords
   * @param y        the coordinates where the iterator's text is to be rendered
   */
  public void drawString( final AttributedCharacterIterator iterator,
                          final float x, final float y ) {
    parent.drawString( iterator, x, y );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param g the <code>GlyphVector</code> to be rendered
   * @param x coordinates.
   * @param y the position in User Space where the glyphs should be rendered
   */
  public void drawGlyphVector( final GlyphVector g, final float x, final float y ) {
    parent.drawGlyphVector( g, x, y );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param s the <code>Shape</code> to be filled
   */
  public void fill( final Shape s ) {
    parent.fill( s );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param rect     the area in device space to check for a hit
   * @param s        the <code>Shape</code> to check for a hit
   * @param onStroke flag used to choose between testing the stroked or the filled shape. If the flag is
   *                 <code>true</code>, the <code>Stroke</code> oultine is tested.  If the flag is <code>false</code>,
   *                 the filled <code>Shape</code> is tested.
   * @return <code>true</code> if there is a hit; <code>false</code> otherwise.
   */
  public boolean hit( final Rectangle rect,
                      final Shape s,
                      final boolean onStroke ) {
    return parent.hit( rect, s, onStroke );
  }

  /**
   * Returns the device configuration associated with this <code>Graphics2D</code>.
   *
   * @return the device configuration of this <code>Graphics2D</code>.
   */
  public GraphicsConfiguration getDeviceConfiguration() {
    return parent.getDeviceConfiguration();
  }

  /**
   * Forwards the call to the parent.
   *
   * @param comp the <code>Composite</code> object to be used for rendering
   * @throws SecurityException if a custom <code>Composite</code> object is being used to render to the screen and a
   *                           security manager is set and its <code>checkPermission</code> method does not allow the
   *                           operation.
   */
  public void setComposite( final Composite comp ) {
    parent.setComposite( comp );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param paint the <code>Paint</code> object to be used to generate color during the rendering process, or
   *              <code>null</code>
   */
  public void setPaint( final Paint paint ) {
    parent.setPaint( paint );
  }

  /**
   * Sets the <code>Stroke</code> for the <code>Graphics2D</code> context.
   *
   * @param s the <code>Stroke</code> object to be used to stroke a <code>Shape</code> during the rendering process
   */
  public void setStroke( final Stroke s ) {
    parent.setStroke( s );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param hintKey   the key of the hint to be set.
   * @param hintValue the value indicating preferences for the specified hint category.
   */
  public void setRenderingHint( final RenderingHints.Key hintKey, final Object hintValue ) {
    parent.setRenderingHint( hintKey, hintValue );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param hintKey the key corresponding to the hint to get.
   * @return an object representing the value for the specified hint key. Some of the keys and their associated values
   * are defined in the <code>RenderingHints</code> class.
   */
  public Object getRenderingHint( final RenderingHints.Key hintKey ) {
    return parent.getRenderingHint( hintKey );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param hints the rendering hints to be set
   */
  public void setRenderingHints( final Map hints ) {
    parent.setRenderingHints( hints );
  }

  /**
   * Forwards the call to the parent.
   *
   * @param hints the rendering hints to be set
   */
  public void addRenderingHints( final Map hints ) {
    parent.addRenderingHints( hints );
  }

  /**
   * Forwards the call to the parent.
   *
   * @return a reference to an instance of <code>RenderingHints</code> that contains the current preferences.
   */
  public RenderingHints getRenderingHints() {
    return parent.getRenderingHints();
  }

  /**
   * Forwards the call to the parent.
   *
   * @param x coords.
   * @param y the specified coordinates
   * @since JDK1.0
   */
  public void translate( final int x, final int y ) {
    parent.translate( x, y );
  }

  /**
   * Concatenates the current <code>Graphics2D</code> <code>Transform</code> with a translation transform. Subsequent
   * rendering is translated by the specified distance relative to the previous position. This is equivalent to calling
   * transform(T), where T is an <code>AffineTransform</code> represented by the following matrix:
   * <pre>
   *  [   1    0    tx  ]
   *  [   0    1    ty  ]
   *  [   0    0    1   ]
   * </pre>
   *
   * @param tx the distance to translate along the x-axis
   * @param ty the distance to translate along the y-axis
   */
  public void translate( final double tx, final double ty ) {
    parent.translate( tx, ty );
  }

  /**
   * Concatenates the current <code>Graphics2D</code> <code>Transform</code> with a rotation transform. Subsequent
   * rendering is rotated by the specified radians relative to the previous origin. This is equivalent to calling
   * <code>transform(R)</code>, where R is an <code>AffineTransform</code> represented by the following matrix:
   * <pre>
   *  [   cos(theta)    -sin(theta)    0   ]
   *  [   sin(theta)     cos(theta)    0   ]
   *  [       0              0         1   ]
   * </pre>
   * Rotating with a positive angle theta rotates points on the positive x axis toward the positive y axis.
   *
   * @param theta the angle of rotation in radians
   */
  public void rotate( final double theta ) {
    parent.rotate( theta );
  }

  /**
   * Concatenates the current <code>Graphics2D</code> <code>Transform</code> with a translated rotation transform.
   * Subsequent rendering is transformed by a transform which is constructed by translating to the specified location,
   * rotating by the specified radians, and translating back by the same amount as the original translation.  This is
   * equivalent to the following sequence of calls:
   * <pre>
   *  translate(x, y);
   *  rotate(theta);
   *  translate(-x, -y);
   * </pre>
   * Rotating with a positive angle theta rotates points on the positive x axis toward the positive y axis.
   *
   * @param theta the angle of rotation in radians
   * @param x     coords.
   * @param y     coordinates of the origin of the rotation
   */
  public void rotate( final double theta, final double x, final double y ) {
    parent.rotate( theta, x, y );
  }

  /**
   * Concatenates the current <code>Graphics2D</code> <code>Transform</code> with a scaling transformation Subsequent
   * rendering is resized according to the specified scaling factors relative to the previous scaling. This is
   * equivalent to calling <code>transform(S)</code>, where S is an <code>AffineTransform</code> represented by the
   * following matrix:
   * <pre>
   *  [   sx   0    0   ]
   *  [   0    sy   0   ]
   *  [   0    0    1   ]
   * </pre>
   *
   * @param sx the amount by which X coordinates in subsequent rendering operations are multiplied relative to previous
   *           rendering operations.
   * @param sy the amount by which Y coordinates in subsequent rendering operations are multiplied relative to previous
   *           rendering operations.
   */
  public void scale( final double sx, final double sy ) {
    parent.scale( sx, sy );
  }

  /**
   * Concatenates the current <code>Graphics2D</code> <code>Transform</code> with a shearing transform. Subsequent
   * renderings are sheared by the specified multiplier relative to the previous position. This is equivalent to calling
   * <code>transform(SH)</code>, where SH is an <code>AffineTransform</code> represented by the following matrix:
   * <pre>
   *  [   1   shx   0   ]
   *  [  shy   1    0   ]
   *  [   0    0    1   ]
   * </pre>
   *
   * @param shx the multiplier by which coordinates are shifted in the positive X axis direction as a function of their
   *            Y coordinate
   * @param shy the multiplier by which coordinates are shifted in the positive Y axis direction as a function of their
   *            X coordinate
   */
  public void shear( final double shx, final double shy ) {
    parent.shear( shx, shy );
  }

  /**
   * Composes an <code>AffineTransform</code> object with the <code>Transform</code> in this <code>Graphics2D</code>
   * according to the rule last-specified-first-applied.  If the current <code>Transform</code> is Cx, the result of
   * composition with Tx is a new <code>Transform</code> Cx'.  Cx' becomes the current <code>Transform</code> for this
   * <code>Graphics2D</code>. Transforming a point p by the updated <code>Transform</code> Cx' is equivalent to first
   * transforming p by Tx and then transforming the result by the original <code>Transform</code> Cx.  In other words,
   * Cx'(p) = Cx(Tx(p)).  A copy of the Tx is made, if necessary, so further modifications to Tx do not affect
   * rendering.
   *
   * @param tx the <code>AffineTransform</code> object to be composed with the current <code>Transform</code>
   * @see #setTransform
   * @see AffineTransform
   */
  public void transform( final AffineTransform tx ) {
    parent.transform( tx );
  }

  /**
   * Overwrites the Transform in the <code>Graphics2D</code> context. WARNING: This method should <b>never</b> be used
   * to apply a new coordinate transform on top of an existing transform because the <code>Graphics2D</code> might
   * already have a transform that is needed for other purposes, such as rendering Swing components or applying a
   * scaling transformation to adjust for the resolution of a printer. <p>To add a coordinate transform, use the
   * <code>transform</code>, <code>rotate</code>, <code>scale</code>, or <code>shear</code> methods.  The
   * <code>setTransform</code> method is intended only for restoring the original <code>Graphics2D</code> transform
   * after rendering, as shown in this example:
   * <pre><blockquote>
   * // Get the current transform
   * AffineTransform saveAT = g2.getTransform();
   * // Perform transformation
   * g2d.transform(...);
   * // Render
   * g2d.draw(...);
   * // Restore original transform
   * g2d.setTransform(saveAT);
   * </blockquote></pre>
   *
   * @param tx the <code>AffineTransform</code> that was retrieved from the <code>getTransform</code> method
   * @see #transform
   * @see #getTransform
   * @see AffineTransform
   */
  public void setTransform( final AffineTransform tx ) {
    parent.setTransform( tx );
  }

  /**
   * Returns a copy of the current <code>Transform</code> in the <code>Graphics2D</code> context.
   *
   * @return the current <code>AffineTransform</code> in the <code>Graphics2D</code> context.
   * @see #transform
   * @see #setTransform
   */
  public AffineTransform getTransform() {
    return parent.getTransform();
  }

  /**
   * Returns the current <code>Paint</code> of the <code>Graphics2D</code> context.
   *
   * @return the current <code>Graphics2D</code> <code>Paint</code>, which defines a color or pattern.
   * @see #setPaint
   * @see Graphics#setColor
   */
  public Paint getPaint() {
    return parent.getPaint();
  }

  /**
   * Returns the current <code>Composite</code> in the <code>Graphics2D</code> context.
   *
   * @return the current <code>Graphics2D</code> <code>Composite</code>, which defines a compositing style.
   * @see #setComposite
   */
  public Composite getComposite() {
    return parent.getComposite();
  }

  /**
   * Sets the background color for the <code>Graphics2D</code> context. The background color is used for clearing a
   * region. When a <code>Graphics2D</code> is constructed for a <code>Component</code>, the background color is
   * inherited from the <code>Component</code>. Setting the background color in the <code>Graphics2D</code> context only
   * affects the subsequent <code>clearRect</code> calls and not the background color of the <code>Component</code>.  To
   * change the background of the <code>Component</code>, use appropriate methods of the <code>Component</code>.
   *
   * @param color the background color that isused in subsequent calls to <code>clearRect</code>
   * @see #getBackground
   * @see Graphics#clearRect
   */
  public void setBackground( final Color color ) {
    parent.setBackground( color );
  }

  /**
   * Returns the background color used for clearing a region.
   *
   * @return the current <code>Graphics2D</code> <code>Color</code>, which defines the background color.
   * @see #setBackground
   */
  public Color getBackground() {
    return parent.getBackground();
  }

  /**
   * Returns the current <code>Stroke</code> in the <code>Graphics2D</code> context.
   *
   * @return the current <code>Graphics2D</code> <code>Stroke</code>, which defines the line style.
   * @see #setStroke
   */
  public Stroke getStroke() {
    return parent.getStroke();
  }

  /**
   * Intersects the current <code>Clip</code> with the interior of the specified <code>Shape</code> and sets the
   * <code>Clip</code> to the resulting intersection.  The specified <code>Shape</code> is transformed with the current
   * <code>Graphics2D</code> <code>Transform</code> before being intersected with the current <code>Clip</code>. This
   * method is used to make the current <code>Clip</code> smaller. To make the <code>Clip</code> larger, use
   * <code>setClip</code>. The <i>user clip</i> modified by this method is independent of the clipping associated with
   * device bounds and visibility.  If no clip has previously been set, or if the clip has been cleared using {@link
   * Graphics#setClip(Shape) setClip} with a <code>null</code> argument, the specified <code>Shape</code> becomes the
   * new user clip.
   *
   * @param s the <code>Shape</code> to be intersected with the current <code>Clip</code>. If <code>s</code> is
   *          <code>null</code>, this method clears the current <code>Clip</code>.
   */
  public void clip( final Shape s ) {
    parent.clip( s );
  }

  /**
   * Get the rendering context of the <code>Font</code> within this <code>Graphics2D</code> context. The {@link
   * FontRenderContext} encapsulates application hints such as anti-aliasing and fractional metrics, as well as target
   * device specific information such as dots-per-inch.  This information should be provided by the application when
   * using objects that perform typographical formatting, such as <code>Font</code> and <code>TextLayout</code>.  This
   * information should also be provided by applications that perform their own layout and need accurate measurements of
   * various characteristics of glyphs such as advance and line height when various rendering hints have been applied to
   * the text rendering.
   *
   * @return a reference to an instance of FontRenderContext.
   * @see FontRenderContext
   * @see Font#createGlyphVector
   * @see java.awt.font.TextLayout
   * @since 1.2
   */

  public FontRenderContext getFontRenderContext() {
    return parent.getFontRenderContext();
  }

  /**
   * Creates a new <code>Graphics</code> object that is a copy of this <code>Graphics</code> object.
   *
   * @return a new graphics context that is a copy of this graphics context.
   */
  public Graphics create() {
    return new BugFixProxyGraphics2D( (Graphics2D) parent.create() );
  }

  /**
   * Gets this graphics context's current color.
   *
   * @return this graphics context's current color.
   * @see Color
   * @see Graphics#setColor(Color)
   */
  public Color getColor() {
    return parent.getColor();
  }

  /**
   * Sets this graphics context's current color to the specified color. All subsequent graphics operations using this
   * graphics context use this specified color.
   *
   * @param c the new rendering color.
   * @see Color
   * @see Graphics#getColor
   */
  public void setColor( final Color c ) {
    parent.setColor( c );
  }

  /**
   * Sets the paint mode of this graphics context to overwrite the destination with this graphics context's current
   * color. This sets the logical pixel operation function to the paint or overwrite mode.  All subsequent rendering
   * operations will overwrite the destination with the current color.
   */
  public void setPaintMode() {
    parent.setPaintMode();
  }

  /**
   * Sets the paint mode of this graphics context to alternate between this graphics context's current color and the new
   * specified color. This specifies that logical pixel operations are performed in the XOR mode, which alternates
   * pixels between the current color and a specified XOR color.
   * <p/>
   * When drawing operations are performed, pixels which are the current color are changed to the specified color, and
   * vice versa.
   * <p/>
   * Pixels that are of colors other than those two colors are changed in an unpredictable but reversible manner; if the
   * same figure is drawn twice, then all pixels are restored to their original values.
   *
   * @param c1 the XOR alternation color
   */
  public void setXORMode( final Color c1 ) {
    parent.setXORMode( c1 );
  }

  /**
   * Gets the current font.
   *
   * @return this graphics context's current font.
   * @see Font
   * @see Graphics#setFont(Font)
   */
  public Font getFont() {
    return parent.getFont();
  }

  /**
   * Sets this graphics context's font to the specified font. All subsequent text operations using this graphics context
   * use this font.
   *
   * @param font the font.
   * @see Graphics#getFont
   * @see Graphics#drawString(String, int, int)
   * @see Graphics#drawBytes(byte[], int, int, int, int)
   * @see Graphics#drawChars(char[], int, int, int, int)
   */
  public void setFont( final Font font ) {
    parent.setFont( font );
  }

  /**
   * Gets the font metrics for the specified font.
   *
   * @param f the specified font
   * @return the font metrics for the specified font.
   * @see Graphics#getFont
   * @see FontMetrics
   * @see Graphics#getFontMetrics()
   */
  public FontMetrics getFontMetrics( final Font f ) {
    return parent.getFontMetrics( f );
  }

  /**
   * Returns the bounding rectangle of the current clipping area. This method refers to the user clip, which is
   * independent of the clipping associated with device bounds and window visibility. If no clip has previously been
   * set, or if the clip has been cleared using <code>setClip(null)</code>, this method returns <code>null</code>. The
   * coordinates in the rectangle are relative to the coordinate system origin of this graphics context.
   *
   * @return the bounding rectangle of the current clipping area, or <code>null</code> if no clip is set.
   * @see Graphics#getClip
   * @see Graphics#clipRect
   * @see Graphics#setClip(int, int, int, int)
   * @see Graphics#setClip(Shape)
   * @since JDK1.1
   */
  public Rectangle getClipBounds() {
    return parent.getClipBounds();
  }

  /**
   * Intersects the current clip with the specified rectangle. The resulting clipping area is the intersection of the
   * current clipping area and the specified rectangle.  If there is no current clipping area, either because the clip
   * has never been set, or the clip has been cleared using <code>setClip(null)</code>, the specified rectangle becomes
   * the new clip. This method sets the user clip, which is independent of the clipping associated with device bounds
   * and window visibility. This method can only be used to make the current clip smaller. To set the current clip
   * larger, use any of the setClip methods. Rendering operations have no effect outside of the clipping area.
   *
   * @param x      the x coordinate of the rectangle to intersect the clip with
   * @param y      the y coordinate of the rectangle to intersect the clip with
   * @param width  the width of the rectangle to intersect the clip with
   * @param height the height of the rectangle to intersect the clip with
   * @see #setClip(int, int, int, int)
   * @see #setClip(Shape)
   */
  public void clipRect( final int x, final int y, final int width, final int height ) {
    // fix a bug in the tree renderer ...
    parent.clip( new Rectangle( x, y, width, height ) );
  }

  /**
   * Sets the current clip to the rectangle specified by the given coordinates.  This method sets the user clip, which
   * is independent of the clipping associated with device bounds and window visibility. Rendering operations have no
   * effect outside of the clipping area.
   *
   * @param x      the <i>x</i> coordinate of the new clip rectangle.
   * @param y      the <i>y</i> coordinate of the new clip rectangle.
   * @param width  the width of the new clip rectangle.
   * @param height the height of the new clip rectangle.
   * @see Graphics#clipRect
   * @see Graphics#setClip(Shape)
   * @see Graphics#getClip
   * @since JDK1.1
   */
  public void setClip( final int x, final int y, final int width, final int height ) {
    parent.setClip( new Rectangle( x, y, width, height ) );
  }

  /**
   * Gets the current clipping area. This method returns the user clip, which is independent of the clipping associated
   * with device bounds and window visibility. If no clip has previously been set, or if the clip has been cleared using
   * <code>setClip(null)</code>, this method returns <code>null</code>.
   *
   * @return a <code>Shape</code> object representing the current clipping area, or <code>null</code> if no clip is set.
   * @see Graphics#getClipBounds
   * @see Graphics#clipRect
   * @see Graphics#setClip(int, int, int, int)
   * @see Graphics#setClip(Shape)
   * @since JDK1.1
   */
  public Shape getClip() {
    return parent.getClip();
  }

  /**
   * Sets the current clipping area to an arbitrary clip shape. Not all objects that implement the <code>Shape</code>
   * interface can be used to set the clip.  The only <code>Shape</code> objects that are guaranteed to be supported are
   * <code>Shape</code> objects that are obtained via the <code>getClip</code> method and via <code>Rectangle</code>
   * objects.  This method sets the user clip, which is independent of the clipping associated with device bounds and
   * window visibility.
   *
   * @param clip the <code>Shape</code> to use to set the clip
   * @see Graphics#getClip()
   * @see Graphics#clipRect
   * @see Graphics#setClip(int, int, int, int)
   * @since JDK1.1
   */
  public void setClip( final Shape clip ) {
    parent.setClip( clip );
  }

  /**
   * Copies an area of the component by a distance specified by <code>dx</code> and <code>dy</code>. From the point
   * specified by <code>x</code> and <code>y</code>, this method copies downwards and to the right.  To copy an area of
   * the component to the left or upwards, specify a negative value for <code>dx</code> or <code>dy</code>. If a portion
   * of the source rectangle lies outside the bounds of the component, or is obscured by another window or component,
   * <code>copyArea</code> will be unable to copy the associated pixels. The area that is omitted can be refreshed by
   * calling the component's <code>paint</code> method.
   *
   * @param x      the <i>x</i> coordinate of the source rectangle.
   * @param y      the <i>y</i> coordinate of the source rectangle.
   * @param width  the width of the source rectangle.
   * @param height the height of the source rectangle.
   * @param dx     the horizontal distance to copy the pixels.
   * @param dy     the vertical distance to copy the pixels.
   */
  public void copyArea( final int x, final int y, final int width, final int height,
                        final int dx, final int dy ) {
    parent.copyArea( x, y, width, height, dx, dy );
  }

  /**
   * Draws a line, using the current color, between the points <code>(x1,&nbsp;y1)</code> and <code>(x2,&nbsp;y2)</code>
   * in this graphics context's coordinate system.
   *
   * @param x1 the first point's <i>x</i> coordinate.
   * @param y1 the first point's <i>y</i> coordinate.
   * @param x2 the second point's <i>x</i> coordinate.
   * @param y2 the second point's <i>y</i> coordinate.
   */
  public void drawLine( final int x1, final int y1, final int x2, final int y2 ) {
    parent.draw( new Line2D.Float( x1, y1, x2, y2 ) );
  }

  /**
   * Fills the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
   * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
   * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
   * wide by <code>height</code> pixels tall. The rectangle is filled using the graphics context's current color.
   *
   * @param x      the <i>x</i> coordinate of the rectangle to be filled.
   * @param y      the <i>y</i> coordinate of the rectangle to be filled.
   * @param width  the width of the rectangle to be filled.
   * @param height the height of the rectangle to be filled.
   * @see Graphics#clearRect
   * @see Graphics#drawRect
   */
  public void fillRect( final int x, final int y, final int width, final int height ) {
    parent.fill( new Rectangle( x, y, width, height ) );
  }

  /**
   * Clears the specified rectangle by filling it with the background color of the current drawing surface. This
   * operation does not use the current paint mode.
   * <p/>
   * Beginning with Java&nbsp;1.1, the background color of offscreen images may be system dependent. Applications should
   * use <code>setColor</code> followed by <code>fillRect</code> to ensure that an offscreen image is cleared to a
   * specific color.
   *
   * @param x      the <i>x</i> coordinate of the rectangle to clear.
   * @param y      the <i>y</i> coordinate of the rectangle to clear.
   * @param width  the width of the rectangle to clear.
   * @param height the height of the rectangle to clear.
   * @see Graphics#fillRect(int, int, int, int)
   * @see Graphics#drawRect
   * @see Graphics#setColor(Color)
   * @see Graphics#setPaintMode
   * @see Graphics#setXORMode(Color)
   */
  public void clearRect( final int x, final int y, final int width, final int height ) {
    parent.clearRect( x, y, width, height );
  }

  /**
   * Draws an outlined round-cornered rectangle using this graphics context's current color. The left and right edges of
   * the rectangle are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>, respectively. The top and bottom edges of
   * the rectangle are at <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
   *
   * @param x         the <i>x</i> coordinate of the rectangle to be drawn.
   * @param y         the <i>y</i> coordinate of the rectangle to be drawn.
   * @param width     the width of the rectangle to be drawn.
   * @param height    the height of the rectangle to be drawn.
   * @param arcWidth  the horizontal diameter of the arc at the four corners.
   * @param arcHeight the vertical diameter of the arc at the four corners.
   * @see Graphics#fillRoundRect
   */
  public void drawRoundRect( final int x, final int y, final int width, final int height,
                             final int arcWidth, final int arcHeight ) {
    parent.draw( new RoundRectangle2D.Float( x, y, width, height, arcWidth, arcHeight ) );
  }

  /**
   * Fills the specified rounded corner rectangle with the current color. The left and right edges of the rectangle are
   * at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>, respectively. The top and bottom edges of the
   * rectangle are at <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
   *
   * @param x         the <i>x</i> coordinate of the rectangle to be filled.
   * @param y         the <i>y</i> coordinate of the rectangle to be filled.
   * @param width     the width of the rectangle to be filled.
   * @param height    the height of the rectangle to be filled.
   * @param arcWidth  the horizontal diameter of the arc at the four corners.
   * @param arcHeight the vertical diameter of the arc at the four corners.
   * @see Graphics#drawRoundRect
   */
  public void fillRoundRect( final int x, final int y, final int width, final int height,
                             final int arcWidth, final int arcHeight ) {
    parent.fill( new RoundRectangle2D.Float( x, y, width, height, arcWidth, arcHeight ) );
  }

  /**
   * Draws the outline of an oval. The result is a circle or ellipse that fits within the rectangle specified by the
   * <code>x</code>, <code>y</code>, <code>width</code>, and <code>height</code> arguments.
   * <p/>
   * The oval covers an area that is <code>width&nbsp;+&nbsp;1</code> pixels wide and <code>height&nbsp;+&nbsp;1</code>
   * pixels tall.
   *
   * @param x      the <i>x</i> coordinate of the upper left corner of the oval to be drawn.
   * @param y      the <i>y</i> coordinate of the upper left corner of the oval to be drawn.
   * @param width  the width of the oval to be drawn.
   * @param height the height of the oval to be drawn.
   * @see Graphics#fillOval
   */
  public void drawOval( final int x, final int y, final int width, final int height ) {
    parent.draw( new Ellipse2D.Float( x, y, width, height ) );

  }

  /**
   * Fills an oval bounded by the specified rectangle with the current color.
   *
   * @param x      the <i>x</i> coordinate of the upper left corner of the oval to be filled.
   * @param y      the <i>y</i> coordinate of the upper left corner of the oval to be filled.
   * @param width  the width of the oval to be filled.
   * @param height the height of the oval to be filled.
   * @see Graphics#drawOval
   */
  public void fillOval( final int x, final int y, final int width, final int height ) {
    parent.fill( new Ellipse2D.Float( x, y, width, height ) );
  }

  /**
   * Draws the outline of a circular or elliptical arc covering the specified rectangle.
   * <p/>
   * The resulting arc begins at <code>startAngle</code> and extends for <code>arcAngle</code> degrees, using the
   * current color. Angles are interpreted such that 0&nbsp;degrees is at the 3&nbsp;o'clock position. A positive value
   * indicates a counter-clockwise rotation while a negative value indicates a clockwise rotation.
   * <p/>
   * The center of the arc is the center of the rectangle whose origin is (<i>x</i>,&nbsp;<i>y</i>) and whose size is
   * specified by the <code>width</code> and <code>height</code> arguments.
   * <p/>
   * The resulting arc covers an area <code>width&nbsp;+&nbsp;1</code> pixels wide by <code>height&nbsp;+&nbsp;1</code>
   * pixels tall.
   * <p/>
   * The angles are specified relative to the non-square extents of the bounding rectangle such that 45 degrees always
   * falls on the line from the center of the ellipse to the upper right corner of the bounding rectangle. As a result,
   * if the bounding rectangle is noticeably longer in one axis than the other, the angles to the start and end of the
   * arc segment will be skewed farther along the longer axis of the bounds.
   *
   * @param x          the <i>x</i> coordinate of the upper-left corner of the arc to be drawn.
   * @param y          the <i>y</i>  coordinate of the upper-left corner of the arc to be drawn.
   * @param width      the width of the arc to be drawn.
   * @param height     the height of the arc to be drawn.
   * @param startAngle the beginning angle.
   * @param arcAngle   the angular extent of the arc, relative to the start angle.
   * @see Graphics#fillArc
   */
  public void drawArc( final int x, final int y, final int width, final int height,
                       final int startAngle, final int arcAngle ) {
    parent.fill( new Arc2D.Float( x, y, width, height, startAngle, arcAngle, Arc2D.PIE ) );
  }

  /**
   * Fills a circular or elliptical arc covering the specified rectangle.
   * <p/>
   * The resulting arc begins at <code>startAngle</code> and extends for <code>arcAngle</code> degrees. Angles are
   * interpreted such that 0&nbsp;degrees is at the 3&nbsp;o'clock position. A positive value indicates a
   * counter-clockwise rotation while a negative value indicates a clockwise rotation.
   * <p/>
   * The center of the arc is the center of the rectangle whose origin is (<i>x</i>,&nbsp;<i>y</i>) and whose size is
   * specified by the <code>width</code> and <code>height</code> arguments.
   * <p/>
   * The resulting arc covers an area <code>width&nbsp;+&nbsp;1</code> pixels wide by <code>height&nbsp;+&nbsp;1</code>
   * pixels tall.
   * <p/>
   * The angles are specified relative to the non-square extents of the bounding rectangle such that 45 degrees always
   * falls on the line from the center of the ellipse to the upper right corner of the bounding rectangle. As a result,
   * if the bounding rectangle is noticeably longer in one axis than the other, the angles to the start and end of the
   * arc segment will be skewed farther along the longer axis of the bounds.
   *
   * @param x          the <i>x</i> coordinate of the upper-left corner of the arc to be filled.
   * @param y          the <i>y</i>  coordinate of the upper-left corner of the arc to be filled.
   * @param width      the width of the arc to be filled.
   * @param height     the height of the arc to be filled.
   * @param startAngle the beginning angle.
   * @param arcAngle   the angular extent of the arc, relative to the start angle.
   * @see Graphics#drawArc
   */
  public void fillArc( final int x, final int y, final int width, final int height,
                       final int startAngle, final int arcAngle ) {
    parent.fill( new Arc2D.Float( x, y, width, height, startAngle, arcAngle, Arc2D.PIE ) );
  }

  /**
   * Draws a sequence of connected lines defined by arrays of <i>x</i> and <i>y</i> coordinates. Each pair of
   * (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point. The figure is not closed if the first point differs from the
   * last point.
   *
   * @param xPoints an array of <i>x</i> points
   * @param yPoints an array of <i>y</i> points
   * @param nPoints the total number of points
   * @see Graphics#drawPolygon(int[], int[], int)
   * @since JDK1.1
   */
  public void drawPolyline( final int[] xPoints, final int[] yPoints,
                            final int nPoints ) {
    parent.drawPolyline( xPoints, yPoints, nPoints );

  }

  /**
   * Draws a closed polygon defined by arrays of <i>x</i> and <i>y</i> coordinates. Each pair of
   * (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
   * <p/>
   * This method draws the polygon defined by <code>nPoint</code> line segments, where the first
   * <code>nPoint&nbsp;-&nbsp;1</code> line segments are line segments from <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;
   * yPoints[i&nbsp;-&nbsp;1])</code>
   * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
   * The figure is automatically closed by drawing a line connecting the final point to the first point, if those points
   * are different.
   *
   * @param xPoints a an array of <code>x</code> coordinates.
   * @param yPoints a an array of <code>y</code> coordinates.
   * @param nPoints a the total number of points.
   * @see Graphics#fillPolygon
   * @see Graphics#drawPolyline
   */
  public void drawPolygon( final int[] xPoints, final int[] yPoints,
                           final int nPoints ) {
    parent.draw( new Polygon( xPoints, yPoints, nPoints ) );
  }

  /**
   * Fills a closed polygon defined by arrays of <i>x</i> and <i>y</i> coordinates.
   * <p/>
   * This method draws the polygon defined by <code>nPoint</code> line segments, where the first
   * <code>nPoint&nbsp;-&nbsp;1</code> line segments are line segments from <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;
   * yPoints[i&nbsp;-&nbsp;1])</code>
   * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.
   * The figure is automatically closed by drawing a line connecting the final point to the first point, if those points
   * are different.
   * <p/>
   * The area inside the polygon is defined using an even-odd fill rule, also known as the alternating rule.
   *
   * @param xPoints a an array of <code>x</code> coordinates.
   * @param yPoints a an array of <code>y</code> coordinates.
   * @param nPoints a the total number of points.
   * @see Graphics#drawPolygon(int[], int[], int)
   */
  public void fillPolygon( final int[] xPoints, final int[] yPoints,
                           final int nPoints ) {
    parent.fill( new Polygon( xPoints, yPoints, nPoints ) );
  }

  /**
   * Draws as much of the specified image as is currently available. The image is drawn with its top-left corner at
   * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate space. Transparent pixels in the image do not
   * affect whatever pixels are already there.
   * <p/>
   * This method returns immediately in all cases, even if the complete image has not yet been loaded, and it has not
   * been dithered and converted for the current output device.
   * <p/>
   * If the image has not yet been completely loaded, then <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies the specified image observer.
   *
   * @param img      the specified image to be drawn.
   * @param x        the <i>x</i> coordinate.
   * @param y        the <i>y</i> coordinate.
   * @param observer object to be notified as more of the image is converted.
   * @return <code>true</code> if the image is completely loaded; <code>false</code> otherwise.
   * @see Image
   * @see ImageObserver
   * @see ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage( final Image img, final int x, final int y,
                            final ImageObserver observer ) {
    return parent.drawImage( img, x, y, observer );
  }

  /**
   * Draws as much of the specified image as has already been scaled to fit inside the specified rectangle.
   * <p/>
   * The image is drawn inside the specified rectangle of this graphics context's coordinate space, and is scaled if
   * necessary. Transparent pixels do not affect whatever pixels are already there.
   * <p/>
   * This method returns immediately in all cases, even if the entire image has not yet been scaled, dithered, and
   * converted for the current output device. If the current output representation is not yet complete, then
   * <code>drawImage</code> returns <code>false</code>. As more of the image becomes available, the process that draws
   * the image notifies the image observer by calling its <code>imageUpdate</code> method.
   * <p/>
   * A scaled version of an image will not necessarily be available immediately just because an unscaled version of the
   * image has been constructed for this output device. Each size of the image may be cached separately and generated
   * from the original data in a separate image production sequence.
   *
   * @param img      the specified image to be drawn.
   * @param x        the <i>x</i> coordinate.
   * @param y        the <i>y</i> coordinate.
   * @param width    the width of the rectangle.
   * @param height   the height of the rectangle.
   * @param observer object to be notified as more of the image is converted.
   * @return <code>true</code> if the current output representation is complete; <code>false</code> otherwise.
   * @see Image
   * @see ImageObserver
   * @see ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage( final Image img, final int x, final int y,
                            final int width, final int height,
                            final ImageObserver observer ) {
    return parent.drawImage( img, x, y, width, height, observer );
  }

  /**
   * Draws as much of the specified image as is currently available. The image is drawn with its top-left corner at
   * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate space.  Transparent pixels are drawn in the
   * specified background color.
   * <p/>
   * This operation is equivalent to filling a rectangle of the width and height of the specified image with the given
   * color and then drawing the image on top of it, but possibly more efficient.
   * <p/>
   * This method returns immediately in all cases, even if the complete image has not yet been loaded, and it has not
   * been dithered and converted for the current output device.
   * <p/>
   * If the image has not yet been completely loaded, then <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies the specified image observer.
   *
   * @param img      the specified image to be drawn.
   * @param x        the <i>x</i> coordinate.
   * @param y        the <i>y</i> coordinate.
   * @param bgcolor  the background color to paint under the non-opaque portions of the image.
   * @param observer object to be notified as more of the image is converted.
   * @return <code>true</code> if the image is completely loaded; <code>false</code> otherwise.
   * @see Image
   * @see ImageObserver
   * @see ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage( final Image img, final int x, final int y,
                            final Color bgcolor,
                            final ImageObserver observer ) {
    return parent.drawImage( img, x, y, bgcolor, observer );
  }

  /**
   * Draws as much of the specified image as has already been scaled to fit inside the specified rectangle.
   * <p/>
   * The image is drawn inside the specified rectangle of this graphics context's coordinate space, and is scaled if
   * necessary. Transparent pixels are drawn in the specified background color. This operation is equivalent to filling
   * a rectangle of the width and height of the specified image with the given color and then drawing the image on top
   * of it, but possibly more efficient.
   * <p/>
   * This method returns immediately in all cases, even if the entire image has not yet been scaled, dithered, and
   * converted for the current output device. If the current output representation is not yet complete then
   * <code>drawImage</code> returns <code>false</code>. As more of the image becomes available, the process that draws
   * the image notifies the specified image observer.
   * <p/>
   * A scaled version of an image will not necessarily be available immediately just because an unscaled version of the
   * image has been constructed for this output device. Each size of the image may be cached separately and generated
   * from the original data in a separate image production sequence.
   *
   * @param img      the specified image to be drawn.
   * @param x        the <i>x</i> coordinate.
   * @param y        the <i>y</i> coordinate.
   * @param width    the width of the rectangle.
   * @param height   the height of the rectangle.
   * @param bgcolor  the background color to paint under the non-opaque portions of the image.
   * @param observer object to be notified as more of the image is converted.
   * @return <code>true</code> if the current output representation is complete; <code>false</code> otherwise.
   * @see Image
   * @see ImageObserver
   * @see ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage( final Image img, final int x, final int y,
                            final int width, final int height,
                            final Color bgcolor,
                            final ImageObserver observer ) {
    return parent.drawImage( img, x, y, width, height, bgcolor, observer );
  }

  /**
   * Draws as much of the specified area of the specified image as is currently available, scaling it on the fly to fit
   * inside the specified area of the destination drawable surface. Transparent pixels do not affect whatever pixels are
   * already there.
   * <p/>
   * This method returns immediately in all cases, even if the image area to be drawn has not yet been scaled, dithered,
   * and converted for the current output device. If the current output representation is not yet complete then
   * <code>drawImage</code> returns <code>false</code>. As more of the image becomes available, the process that draws
   * the image notifies the specified image observer.
   * <p/>
   * This method always uses the unscaled version of the image to render the scaled rectangle and performs the required
   * scaling on the fly. It does not use a cached, scaled version of the image for this operation. Scaling of the image
   * from source to destination is performed such that the first coordinate of the source rectangle is mapped to the
   * first coordinate of the destination rectangle, and the second source coordinate is mapped to the second destination
   * coordinate. The subimage is scaled and flipped as needed to preserve those mappings.
   *
   * @param img      the specified image to be drawn
   * @param dx1      the <i>x</i> coordinate of the first corner of the destination rectangle.
   * @param dy1      the <i>y</i> coordinate of the first corner of the destination rectangle.
   * @param dx2      the <i>x</i> coordinate of the second corner of the destination rectangle.
   * @param dy2      the <i>y</i> coordinate of the second corner of the destination rectangle.
   * @param sx1      the <i>x</i> coordinate of the first corner of the source rectangle.
   * @param sy1      the <i>y</i> coordinate of the first corner of the source rectangle.
   * @param sx2      the <i>x</i> coordinate of the second corner of the source rectangle.
   * @param sy2      the <i>y</i> coordinate of the second corner of the source rectangle.
   * @param observer object to be notified as more of the image is scaled and converted.
   * @return <code>true</code> if the current output representation is complete; <code>false</code> otherwise.
   * @see Image
   * @see ImageObserver
   * @see ImageObserver#imageUpdate(Image, int, int, int, int, int)
   * @since JDK1.1
   */
  public boolean drawImage( final Image img,
                            final int dx1, final int dy1, final int dx2, final int dy2,
                            final int sx1, final int sy1, final int sx2, final int sy2,
                            final ImageObserver observer ) {
    return parent.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer );
  }

  /**
   * Draws as much of the specified area of the specified image as is currently available, scaling it on the fly to fit
   * inside the specified area of the destination drawable surface.
   * <p/>
   * Transparent pixels are drawn in the specified background color. This operation is equivalent to filling a rectangle
   * of the width and height of the specified image with the given color and then drawing the image on top of it, but
   * possibly more efficient.
   * <p/>
   * This method returns immediately in all cases, even if the image area to be drawn has not yet been scaled, dithered,
   * and converted for the current output device. If the current output representation is not yet complete then
   * <code>drawImage</code> returns <code>false</code>. As more of the image becomes available, the process that draws
   * the image notifies the specified image observer.
   * <p/>
   * This method always uses the unscaled version of the image to render the scaled rectangle and performs the required
   * scaling on the fly. It does not use a cached, scaled version of the image for this operation. Scaling of the image
   * from source to destination is performed such that the first coordinate of the source rectangle is mapped to the
   * first coordinate of the destination rectangle, and the second source coordinate is mapped to the second destination
   * coordinate. The subimage is scaled and flipped as needed to preserve those mappings.
   *
   * @param img      the specified image to be drawn
   * @param dx1      the <i>x</i> coordinate of the first corner of the destination rectangle.
   * @param dy1      the <i>y</i> coordinate of the first corner of the destination rectangle.
   * @param dx2      the <i>x</i> coordinate of the second corner of the destination rectangle.
   * @param dy2      the <i>y</i> coordinate of the second corner of the destination rectangle.
   * @param sx1      the <i>x</i> coordinate of the first corner of the source rectangle.
   * @param sy1      the <i>y</i> coordinate of the first corner of the source rectangle.
   * @param sx2      the <i>x</i> coordinate of the second corner of the source rectangle.
   * @param sy2      the <i>y</i> coordinate of the second corner of the source rectangle.
   * @param bgcolor  the background color to paint under the non-opaque portions of the image.
   * @param observer object to be notified as more of the image is scaled and converted.
   * @return <code>true</code> if the current output representation is complete; <code>false</code> otherwise.
   * @see Image
   * @see ImageObserver
   * @see ImageObserver#imageUpdate(Image, int, int, int, int, int)
   * @since JDK1.1
   */
  public boolean drawImage( final Image img,
                            final int dx1, final int dy1, final int dx2, final int dy2,
                            final int sx1, final int sy1, final int sx2, final int sy2,
                            final Color bgcolor,
                            final ImageObserver observer ) {
    return parent.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer );
  }

  /**
   * Disposes of this graphics context and releases any system resources that it is using. A <code>Graphics</code>
   * object cannot be used after <code>dispose</code>has been called.
   * <p/>
   * When a Java program runs, a large number of <code>Graphics</code> objects can be created within a short time frame.
   * Although the finalization process of the garbage collector also disposes of the same system resources, it is
   * preferable to manually free the associated resources by calling this method rather than to rely on a finalization
   * process which may not run to completion for a long period of time.
   * <p/>
   * Graphics objects which are provided as arguments to the <code>paint</code> and <code>update</code> methods of
   * components are automatically released by the system when those methods return. For efficiency, programmers should
   * call <code>dispose</code> when finished using a <code>Graphics</code> object only if it was created directly from a
   * component or another <code>Graphics</code> object.
   *
   * @see Graphics#finalize
   * @see java.awt.Component#paint
   * @see java.awt.Component#update
   * @see java.awt.Component#getGraphics
   * @see Graphics#create
   */
  public void dispose() {
    parent.dispose();
  }

  /**
   * Draws the outline of the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
   * <code>x&nbsp;+&nbsp;width</code>. The top and bottom edges are at <code>y</code> and
   * <code>y&nbsp;+&nbsp;height</code>. The rectangle is drawn using the graphics context's current color.
   *
   * @param x      the <i>x</i> coordinate of the rectangle to be drawn.
   * @param y      the <i>y</i> coordinate of the rectangle to be drawn.
   * @param width  the width of the rectangle to be drawn.
   * @param height the height of the rectangle to be drawn.
   * @see Graphics#fillRect
   * @see Graphics#clearRect
   */
  public void drawRect( final int x, final int y, final int width, final int height ) {
    final Rectangle rect = new Rectangle( x, y, width, height );
    if ( parent.getClip().intersects( rect ) ) {
      parent.draw( rect );
    }
  }

  /**
   * Draws the outline of a polygon defined by the specified <code>Polygon</code> object.
   *
   * @param p the polygon to draw.
   * @see Graphics#fillPolygon
   * @see Graphics#drawPolyline
   */
  public void drawPolygon( final Polygon p ) {
    parent.draw( p );
  }

  /**
   * Fills the polygon defined by the specified Polygon object with the graphics context's current color.
   * <p/>
   * The area inside the polygon is defined using an even-odd fill rule, also known as the alternating rule.
   *
   * @param p the polygon to fill.
   * @see Graphics#drawPolygon(int[], int[], int)
   */
  public void fillPolygon( final Polygon p ) {
    parent.fill( p );
  }
}
