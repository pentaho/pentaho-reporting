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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.fonts.itext.BaseFontFontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;

/**
 * This file is a temporary fix to an iText-Bug. Please do not rely on the existence of this file, as soon as there is
 * an officially fixed iText version, this file will go away.
 *
 * @noinspection HardCodedStringLiteral
 */
public class PdfGraphics2D extends Graphics2D {
  private static final Log logger = LogFactory.getLog( PdfGraphics2D.class );

  private static final int FILL = 1;
  private static final int STROKE = 2;
  private static final int CLIP = 3;
  private BasicStroke strokeOne = new BasicStroke( 1 );

  private static final AffineTransform IDENTITY = new AffineTransform();

  private Font font;
  private BaseFont lastBaseFont;
  private AffineTransform transform;
  private Paint paint;
  private Color background;
  private float width;
  private float height;

  private Area clip;

  private RenderingHints rhints = new RenderingHints( null );

  private Stroke stroke;
  private Stroke originalStroke;

  private PdfContentByte cb;

  private boolean disposeCalled;

  private ArrayList kids;

  private PdfGraphics2D parent;

  private Graphics2D dg2 = new BufferedImage( 2, 2, BufferedImage.TYPE_INT_RGB ).createGraphics();

  private Stroke oldStroke;
  private Paint paintFill;
  private Paint paintStroke;

  private MediaTracker mediaTracker;

  // Added by Jurij Bilas
  private boolean underline; // indicates if the font style is underlined

  private PdfGState[] fillGState = new PdfGState[256];
  private PdfGState[] strokeGState = new PdfGState[256];
  private int currentFillGState = 255;
  private int currentStrokeGState = 255;

  public static final int AFM_DIVISOR = 1000; // used to calculate coordinates

  // Added by Alexej Suchov
  private float alpha;

  // Added by Alexej Suchov
  private Composite composite;

  // Added by Alexej Suchov
  private Paint realPaint;
  private PdfOutputProcessorMetaData metaData;
  private static final AffineTransform FLIP_TRANSFORM = AffineTransform.getScaleInstance( 1, -1 );

  private PdfGraphics2D() {
    dg2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
    setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
  }

  /**
   * Constructor for PDFGraphics2D.
   */
  public PdfGraphics2D( final PdfContentByte cb, final float width, final float height,
      final PdfOutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    dg2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
    setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );

    this.transform = new AffineTransform();

    paint = Color.black;
    background = Color.white;
    setFont( new Font( "sanserif", Font.PLAIN, 12 ) );
    this.cb = cb;
    cb.saveState();
    this.width = width;
    this.height = height;
    clip = new Area( new Rectangle2D.Float( 0, 0, width, height ) );
    clip( clip );
    oldStroke = strokeOne;
    stroke = strokeOne;
    originalStroke = strokeOne;
    setStrokeDiff( stroke, null );
    cb.saveState();
  }

  public PdfContentByte getRawContentByte() {
    return cb;
  }

  /**
   * @see Graphics2D#draw(Shape)
   */
  @Override
  public void draw( final Shape s ) {
    followPath( s, PdfGraphics2D.STROKE );
  }

  /**
   * @see Graphics2D#drawImage(Image, AffineTransform, ImageObserver)
   */
  @Override
  public boolean drawImage( final Image img, final AffineTransform xform, final ImageObserver obs ) {
    return drawImage( img, null, xform, null, obs );
  }

  /**
   * @see Graphics2D#drawImage(BufferedImage, BufferedImageOp, int, int)
   */
  @Override
  public void drawImage( final BufferedImage img, final BufferedImageOp op, final int x, final int y ) {
    BufferedImage result = img;
    if ( op != null ) {
      result = op.createCompatibleDestImage( img, img.getColorModel() );
      result = op.filter( img, result );
    }
    drawImage( result, x, y, null );
  }

  /**
   * @noinspection UseOfObsoleteCollectionType
   * @see Graphics2D#drawRenderedImage(RenderedImage, AffineTransform)
   */
  @Override
  public void drawRenderedImage( final RenderedImage img, final AffineTransform xform ) {
    final BufferedImage image;
    if ( img instanceof BufferedImage ) {
      image = (BufferedImage) img;
    } else {
      final ColorModel cm = img.getColorModel();
      final int width = img.getWidth();
      final int height = img.getHeight();
      final WritableRaster raster = cm.createCompatibleWritableRaster( width, height );
      final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
      final Hashtable properties = new Hashtable();
      final String[] keys = img.getPropertyNames();
      if ( keys != null ) {
        final int keyCount = keys.length;
        for ( int i = 0; i < keyCount; i++ ) {
          properties.put( keys[i], img.getProperty( keys[i] ) );
        }
      }
      final BufferedImage result = new BufferedImage( cm, raster, isAlphaPremultiplied, properties );
      img.copyData( raster );
      image = result;
    }
    drawImage( image, xform, null );
  }

  /**
   * @see Graphics2D#drawRenderableImage(RenderableImage, AffineTransform)
   */
  @Override
  public void drawRenderableImage( final RenderableImage img, final AffineTransform xform ) {
    drawRenderedImage( img.createDefaultRendering(), xform );
  }

  /**
   * @see Graphics#drawString(String, int, int)
   */
  @Override
  public void drawString( final String s, final int x, final int y ) {
    drawString( s, (float) x, (float) y );
  }

  /**
   * Calculates position and/or stroke thickness depending on the font size
   *
   * @param d
   *          value to be converted
   * @param i
   *          font size
   * @return position and/or stroke thickness depending on the font size
   */
  private static double asPoints( final double d, final int i ) {
    return ( d * i ) / AFM_DIVISOR;
  }

  /**
   * This routine goes through the attributes and sets the font before calling the actual string drawing routine
   *
   * @param iter
   */
  private void doAttributes( final AttributedCharacterIterator iter ) {
    underline = false;
    final Set set = iter.getAttributes().keySet();
    for ( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
      final AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute) iterator.next();
      if ( !( attribute instanceof TextAttribute ) ) {
        continue;
      }
      final TextAttribute textattribute = (TextAttribute) attribute;
      if ( textattribute.equals( TextAttribute.FONT ) ) {
        final Font font = (Font) iter.getAttributes().get( textattribute );
        setFont( font );
      } else if ( textattribute.equals( TextAttribute.UNDERLINE ) ) {
        if ( iter.getAttributes().get( textattribute ) == TextAttribute.UNDERLINE_ON ) {
          underline = true;
        }
      } else if ( textattribute.equals( TextAttribute.SIZE ) ) {
        final Object obj = iter.getAttributes().get( textattribute );
        if ( obj instanceof Integer ) {
          final int i = ( (Integer) obj ).intValue();
          setFont( getFont().deriveFont( getFont().getStyle(), i ) );
        } else if ( obj instanceof Float ) {
          final float f = ( (Float) obj ).floatValue();
          setFont( getFont().deriveFont( getFont().getStyle(), f ) );
        }
      } else if ( textattribute.equals( TextAttribute.FOREGROUND ) ) {
        setColor( (Color) iter.getAttributes().get( textattribute ) );
      } else if ( textattribute.equals( TextAttribute.FAMILY ) ) {
        final Font font = getFont();
        final Map fontAttributes = font.getAttributes();
        fontAttributes.put( TextAttribute.FAMILY, iter.getAttributes().get( textattribute ) );
        setFont( font.deriveFont( fontAttributes ) );
      } else if ( textattribute.equals( TextAttribute.POSTURE ) ) {
        final Font font = getFont();
        final Map fontAttributes = font.getAttributes();
        fontAttributes.put( TextAttribute.POSTURE, iter.getAttributes().get( textattribute ) );
        setFont( font.deriveFont( fontAttributes ) );
      } else if ( textattribute.equals( TextAttribute.WEIGHT ) ) {
        final Font font = getFont();
        final Map fontAttributes = font.getAttributes();
        fontAttributes.put( TextAttribute.WEIGHT, iter.getAttributes().get( textattribute ) );
        setFont( font.deriveFont( fontAttributes ) );
      }
    }
  }

  @Override
  public void drawString( final String s, final float x, float y ) {
    if ( s.length() == 0 ) {
      return;
    }
    setFillPaint();
    setStrokePaint();

    final AffineTransform at = getTransform();
    final AffineTransform at2 = getTransform();
    at2.translate( x, y );
    at2.concatenate( font.getTransform() );
    setTransform( at2 );
    final AffineTransform inverse = this.normalizeMatrix();
    final AffineTransform flipper = FLIP_TRANSFORM;
    inverse.concatenate( flipper );
    final double[] mx = new double[6];
    inverse.getMatrix( mx );
    cb.beginText();

    final float fontSize = font.getSize2D();
    if ( lastBaseFont == null ) {
      final String fontName = font.getName();
      final boolean bold = font.isBold();
      final boolean italic = font.isItalic();

      final BaseFontFontMetrics fontMetrics =
          metaData.getBaseFontFontMetrics( fontName, fontSize, bold, italic, null, metaData
              .isFeatureSupported( OutputProcessorFeature.EMBED_ALL_FONTS ), false );
      final FontNativeContext nativeContext = fontMetrics.getNativeContext();
      lastBaseFont = fontMetrics.getBaseFont();

      cb.setFontAndSize( lastBaseFont, fontSize );
      if ( fontMetrics.isTrueTypeFont() && bold && nativeContext.isNativeBold() == false ) {
        final float strokeWidth = font.getSize2D() / 30.0f; // right from iText ...
        if ( strokeWidth == 1 ) {
          cb.setTextRenderingMode( PdfContentByte.TEXT_RENDER_MODE_FILL );
        } else {
          cb.setTextRenderingMode( PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE );
          cb.setLineWidth( strokeWidth );
        }
      } else {
        cb.setTextRenderingMode( PdfContentByte.TEXT_RENDER_MODE_FILL );
      }
    } else {
      cb.setFontAndSize( lastBaseFont, fontSize );
    }

    cb.setTextMatrix( (float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4], (float) mx[5] );
    double width = 0;
    if ( fontSize > 0 ) {
      final float scale = 1000 / fontSize;
      final Font font = this.font.deriveFont( AffineTransform.getScaleInstance( scale, scale ) );
      final Rectangle2D stringBounds = font.getStringBounds( s, getFontRenderContext() );
      width = stringBounds.getWidth() / scale;
    }
    if ( s.length() > 1 ) {
      final float adv = ( (float) width - lastBaseFont.getWidthPoint( s, fontSize ) ) / ( s.length() - 1 );
      cb.setCharacterSpacing( adv );
    }
    cb.showText( s );
    if ( s.length() > 1 ) {
      cb.setCharacterSpacing( 0 );
    }
    cb.endText();
    setTransform( at );
    if ( underline ) {
      // These two are supposed to be taken from the .AFM file
      // int UnderlinePosition = -100;
      final int UnderlineThickness = 50;
      //
      final double d = PdfGraphics2D.asPoints( UnderlineThickness, (int) fontSize );
      setStroke( new BasicStroke( (float) d ) );
      y = (float) ( ( y ) + PdfGraphics2D.asPoints( ( UnderlineThickness ), (int) fontSize ) );
      final Line2D line = new Line2D.Double( x, y, ( width + x ), y );
      draw( line );
    }
  }

  /**
   * @see Graphics#drawString(AttributedCharacterIterator, int, int)
   */
  @Override
  public void drawString( final AttributedCharacterIterator iterator, final int x, final int y ) {
    drawString( iterator, (float) x, (float) y );
  }

  /**
   * @see Graphics2D#drawString(AttributedCharacterIterator, float, float)
   */
  @Override
  public void drawString( final AttributedCharacterIterator iter, float x, final float y ) {
    /*
     * StringBuffer sb = new StringBuffer(); for(char c = iter.first(); c != AttributedCharacterIterator.DONE; c =
     * iter.next()) { sb.append(c); } drawString(sb.toString(),x,y);
     */
    final StringBuilder stringbuffer = new StringBuilder( iter.getEndIndex() );
    for ( char c = iter.first(); c != AttributedCharacterIterator.DONE; c = iter.next() ) {
      if ( iter.getIndex() == iter.getRunStart() ) {
        if ( stringbuffer.length() > 0 ) {
          drawString( stringbuffer.toString(), x, y );
          final FontMetrics fontmetrics = getFontMetrics();
          x = (float) ( x + fontmetrics.getStringBounds( stringbuffer.toString(), this ).getWidth() );
          stringbuffer.delete( 0, stringbuffer.length() );
        }
        doAttributes( iter );
      }
      stringbuffer.append( c );
    }

    drawString( stringbuffer.toString(), x, y );
    underline = false;
  }

  /**
   * @see Graphics2D#drawGlyphVector(GlyphVector, float, float)
   */
  @Override
  public void drawGlyphVector( final GlyphVector g, final float x, final float y ) {
    final Shape s = g.getOutline( x, y );
    fill( s );
  }

  /**
   * @see Graphics2D#fill(Shape)
   */
  @Override
  public void fill( final Shape s ) {
    followPath( s, PdfGraphics2D.FILL );
  }

  /**
   * @see Graphics2D#hit(Rectangle, Shape, boolean)
   */
  @Override
  public boolean hit( final Rectangle rect, Shape s, final boolean onStroke ) {
    if ( onStroke ) {
      s = stroke.createStrokedShape( s );
    }
    s = transform.createTransformedShape( s );
    final Area area = new Area( s );
    if ( clip != null ) {
      area.intersect( clip );
    }
    return area.intersects( rect.x, rect.y, rect.width, rect.height );
  }

  /**
   * @see Graphics2D#getDeviceConfiguration()
   */
  @Override
  public GraphicsConfiguration getDeviceConfiguration() {
    return dg2.getDeviceConfiguration();
  }

  /**
   * Method contributed by Alexej Suchov
   *
   * @see Graphics2D#setComposite(Composite)
   */
  @Override
  public void setComposite( final Composite comp ) {

    if ( comp instanceof AlphaComposite ) {

      final AlphaComposite composite = (AlphaComposite) comp;

      if ( composite.getRule() == 3 ) {

        alpha = composite.getAlpha();
        this.composite = composite;

        if ( realPaint != null && ( realPaint instanceof Color ) ) {

          final Color c = (Color) realPaint;
          paint = new Color( c.getRed(), c.getGreen(), c.getBlue(), (int) ( c.getAlpha() * alpha ) );
        }
        return;
      }
    }

    this.composite = comp;
    alpha = 1.0F;

  }

  /**
   * Method contributed by Alexej Suchov
   *
   * @see Graphics2D#setPaint(Paint)
   */
  @Override
  public void setPaint( final Paint paint ) {
    if ( paint == null ) {
      return;
    }
    this.paint = paint;
    realPaint = paint;

    if ( ( composite instanceof AlphaComposite ) && ( paint instanceof Color ) ) {

      final AlphaComposite co = (AlphaComposite) composite;

      if ( co.getRule() == 3 ) {
        final Color c = (Color) paint;
        this.paint = new Color( c.getRed(), c.getGreen(), c.getBlue(), (int) ( c.getAlpha() * alpha ) );
        realPaint = paint;
      }
    }

  }

  private Stroke transformStroke( final Stroke stroke ) {
    if ( !( stroke instanceof BasicStroke ) ) {
      return stroke;
    }
    final BasicStroke st = (BasicStroke) stroke;
    final float scale = (float) Math.sqrt( Math.abs( transform.getDeterminant() ) );
    final float[] dash = st.getDashArray();
    if ( dash != null ) {
      final int dashLength = dash.length;
      for ( int k = 0; k < dashLength; ++k ) {
        dash[k] *= scale;
      }
    }
    return new BasicStroke( st.getLineWidth() * scale, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), dash, st
        .getDashPhase()
        * scale );
  }

  private void setStrokeDiff( final Stroke newStroke, final Stroke oldStroke ) {
    if ( newStroke == oldStroke ) {
      return;
    }
    if ( !( newStroke instanceof BasicStroke ) ) {
      return;
    }
    final BasicStroke nStroke = (BasicStroke) newStroke;
    final boolean oldOk = ( oldStroke instanceof BasicStroke );
    BasicStroke oStroke = null;
    if ( oldOk ) {
      oStroke = (BasicStroke) oldStroke;
    }
    if ( !oldOk || nStroke.getLineWidth() != oStroke.getLineWidth() ) {
      cb.setLineWidth( nStroke.getLineWidth() );
    }
    if ( !oldOk || nStroke.getEndCap() != oStroke.getEndCap() ) {
      switch ( nStroke.getEndCap() ) {
        case BasicStroke.CAP_BUTT:
          cb.setLineCap( 0 );
          break;
        case BasicStroke.CAP_SQUARE:
          cb.setLineCap( 2 );
          break;
        default:
          cb.setLineCap( 1 );
      }
    }
    if ( !oldOk || nStroke.getLineJoin() != oStroke.getLineJoin() ) {
      switch ( nStroke.getLineJoin() ) {
        case BasicStroke.JOIN_MITER:
          cb.setLineJoin( 0 );
          break;
        case BasicStroke.JOIN_BEVEL:
          cb.setLineJoin( 2 );
          break;
        default:
          cb.setLineJoin( 1 );
      }
    }
    if ( !oldOk || nStroke.getMiterLimit() != oStroke.getMiterLimit() ) {
      cb.setMiterLimit( nStroke.getMiterLimit() );
    }
    final boolean makeDash;
    if ( oldOk ) {
      if ( nStroke.getDashArray() != null ) {
        if ( nStroke.getDashPhase() != oStroke.getDashPhase() ) {
          makeDash = true;
        } else if ( !Arrays.equals( nStroke.getDashArray(), oStroke.getDashArray() ) ) {
          makeDash = true;
        } else {
          makeDash = false;
        }
      } else if ( oStroke.getDashArray() != null ) {
        makeDash = true;
      } else {
        makeDash = false;
      }
    } else {
      makeDash = true;
    }
    if ( makeDash ) {
      final float[] dash = nStroke.getDashArray();
      if ( dash == null ) {
        cb.setLiteral( "[]0 d\n" );
      } else {
        cb.setLiteral( '[' );
        final int lim = dash.length;
        for ( int k = 0; k < lim; ++k ) {
          cb.setLiteral( dash[k] );
          cb.setLiteral( ' ' );
        }
        cb.setLiteral( ']' );
        cb.setLiteral( nStroke.getDashPhase() );
        cb.setLiteral( " d\n" );
      }
    }
  }

  /**
   * @see Graphics2D#setStroke(Stroke)
   */
  @Override
  public void setStroke( final Stroke s ) {
    originalStroke = s;
    this.stroke = transformStroke( s );
  }

  /**
   * Sets a rendering hint
   *
   * @param arg0
   * @param arg1
   */
  @Override
  public void setRenderingHint( final RenderingHints.Key arg0, final Object arg1 ) {
    rhints.put( arg0, arg1 );
  }

  /**
   * @param arg0
   *          a key
   * @return the rendering hint
   */
  @Override
  public Object getRenderingHint( final RenderingHints.Key arg0 ) {
    return rhints.get( arg0 );
  }

  /**
   * @see Graphics2D#setRenderingHints(Map)
   */
  @Override
  public void setRenderingHints( final Map hints ) {
    rhints.clear();
    rhints.putAll( hints );
  }

  /**
   * @see Graphics2D#addRenderingHints(Map)
   */
  @Override
  public void addRenderingHints( final Map hints ) {
    rhints.putAll( hints );
  }

  /**
   * @see Graphics2D#getRenderingHints()
   */
  @Override
  public RenderingHints getRenderingHints() {
    return rhints;
  }

  /**
   * @see Graphics#translate(int, int)
   */
  @Override
  public void translate( final int x, final int y ) {
    translate( (double) x, (double) y );
  }

  /**
   * @see Graphics2D#translate(double, double)
   */
  @Override
  public void translate( final double tx, final double ty ) {
    transform.translate( tx, ty );
  }

  /**
   * @see Graphics2D#rotate(double)
   */
  @Override
  public void rotate( final double theta ) {
    transform.rotate( theta );
  }

  /**
   * @see Graphics2D#rotate(double, double, double)
   */
  @Override
  public void rotate( final double theta, final double x, final double y ) {
    transform.rotate( theta, x, y );
  }

  /**
   * @see Graphics2D#scale(double, double)
   */
  @Override
  public void scale( final double sx, final double sy ) {
    transform.scale( sx, sy );
    this.stroke = transformStroke( originalStroke );
  }

  /**
   * @see Graphics2D#shear(double, double)
   */
  @Override
  public void shear( final double shx, final double shy ) {
    transform.shear( shx, shy );
  }

  /**
   * @see Graphics2D#transform(AffineTransform)
   */
  @Override
  public void transform( final AffineTransform tx ) {
    transform.concatenate( tx );
    this.stroke = transformStroke( originalStroke );
  }

  /**
   * @see Graphics2D#setTransform(AffineTransform)
   */
  @Override
  public void setTransform( final AffineTransform t ) {
    transform = new AffineTransform( t );
    this.stroke = transformStroke( originalStroke );
  }

  /**
   * @see Graphics2D#getTransform()
   */
  @Override
  public AffineTransform getTransform() {
    return new AffineTransform( transform );
  }

  /**
   * Method contributed by Alexej Suchov
   *
   * @see Graphics2D#getPaint()
   */
  @Override
  public Paint getPaint() {
    if ( realPaint != null ) {
      return realPaint;
    } else {
      return paint;
    }
  }

  /**
   * @see Graphics2D#getComposite()
   */
  @Override
  public Composite getComposite() {
    return null;
  }

  /**
   * @see Graphics2D#setBackground(Color)
   */
  @Override
  public void setBackground( final Color color ) {
    background = color;
  }

  /**
   * @see Graphics2D#getBackground()
   */
  @Override
  public Color getBackground() {
    return background;
  }

  /**
   * @see Graphics2D#getStroke()
   */
  @Override
  public Stroke getStroke() {
    return originalStroke;
  }

  /**
   * @see Graphics2D#getFontRenderContext()
   */
  @Override
  public FontRenderContext getFontRenderContext() {
    final boolean antialias =
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals( getRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING ) );
    final boolean fractions =
        RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals( getRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS ) );
    return new FontRenderContext( new AffineTransform(), antialias, fractions );
  }

  /**
   * @see Graphics#create()
   */
  @Override
  public Graphics create() {
    final PdfGraphics2D g2 = new PdfGraphics2D();
    g2.transform = new AffineTransform( this.transform );
    g2.metaData = this.metaData;
    g2.paint = this.paint;
    g2.fillGState = this.fillGState;
    g2.strokeGState = this.strokeGState;
    g2.background = this.background;
    g2.mediaTracker = this.mediaTracker;
    g2.setFont( this.font );
    g2.cb = this.cb.getDuplicate();
    g2.cb.saveState();
    g2.width = this.width;
    g2.height = this.height;
    g2.followPath( new Area( new Rectangle2D.Float( 0, 0, width, height ) ), PdfGraphics2D.CLIP );
    if ( this.clip != null ) {
      g2.clip = new Area( this.clip );
    }
    g2.stroke = stroke;
    g2.originalStroke = originalStroke;
    g2.strokeOne = (BasicStroke) g2.transformStroke( g2.strokeOne );
    g2.oldStroke = g2.strokeOne;
    g2.setStrokeDiff( g2.oldStroke, null );
    g2.cb.saveState();
    if ( g2.clip != null ) {
      g2.followPath( g2.clip, PdfGraphics2D.CLIP );
    }
    g2.parent = this;
    if ( this.kids == null ) {
      this.kids = new ArrayList();
    }
    // This is disgusting. You really cant override the buffer on the fixed position. The way the dispose() code
    // handles the cleanup, this will create a huge mess ...
    // this.kids.add(new Integer(cb.getInternalBuffer().size()));
    this.kids.add( g2 );
    return g2;
  }

  public PdfContentByte getContent() {
    return this.cb;
  }

  /**
   * @see Graphics#getColor()
   */
  @Override
  public Color getColor() {
    if ( paint instanceof Color ) {
      return (Color) paint;
    } else {
      return Color.black;
    }
  }

  /**
   * @see Graphics#setColor(Color)
   */
  @Override
  public void setColor( final Color color ) {
    setPaint( color );
  }

  /**
   * @see Graphics#setPaintMode()
   */
  @Override
  public void setPaintMode() {
  }

  /**
   * @see Graphics#setXORMode(Color)
   */
  @Override
  public void setXORMode( final Color c1 ) {

  }

  /**
   * @see Graphics#getFont()
   */
  @Override
  public Font getFont() {
    return font;
  }

  /**
   * @see Graphics#setFont(Font)
   */
  /**
   * Sets the current font.
   */
  @Override
  public void setFont( final Font f ) {
    if ( f == null ) {
      return;
    }

    if ( f == font ) {
      return;
    }
    font = f;
    lastBaseFont = null;

    // baseFont = fontMapper.awtToPdf(f);
  }

  /**
   * @see Graphics#getFontMetrics(Font)
   */
  @Override
  public FontMetrics getFontMetrics( final Font f ) {
    return dg2.getFontMetrics( f );
  }

  /**
   * @see Graphics#getClipBounds()
   */
  @Override
  public Rectangle getClipBounds() {
    if ( clip == null ) {
      return null;
    }
    return getClip().getBounds();
  }

  /**
   * @see Graphics#clipRect(int, int, int, int)
   */
  @Override
  public void clipRect( final int x, final int y, final int width, final int height ) {
    final Rectangle2D rect = new Rectangle2D.Double( x, y, width, height );
    clip( rect );
  }

  /**
   * @see Graphics#setClip(int, int, int, int)
   */
  @Override
  public void setClip( final int x, final int y, final int width, final int height ) {
    final Rectangle2D rect = new Rectangle2D.Double( x, y, width, height );
    setClip( rect );
  }

  /**
   * @see Graphics2D#clip(Shape)
   */
  @Override
  public void clip( Shape s ) {
    if ( s == null ) {
      setClip( null );
      return;
    }
    s = transform.createTransformedShape( s );
    if ( clip == null ) {
      clip = new Area( s );
    } else {
      clip.intersect( new Area( s ) );
    }
    followPath( s, PdfGraphics2D.CLIP );
  }

  /**
   * @see Graphics#getClip()
   */
  @Override
  public Shape getClip() {
    if ( clip == null ) {
      return null;
    }

    try {
      return transform.createInverse().createTransformedShape( clip );
    } catch ( NoninvertibleTransformException e ) {
      return null;
    }
  }

  /**
   * @see Graphics#setClip(Shape)
   */
  @Override
  public void setClip( Shape s ) {
    cb.restoreState();
    cb.saveState();
    if ( s != null ) {
      s = transform.createTransformedShape( s );
    }
    if ( s == null ) {
      clip = null;
    } else {
      clip = new Area( s );
      followPath( s, PdfGraphics2D.CLIP );
    }
    paintFill = null;
    paintStroke = null;
    currentFillGState = 255;
    currentStrokeGState = 255;
    oldStroke = strokeOne;
  }

  /**
   * @see Graphics#copyArea(int, int, int, int, int, int)
   */
  @Override
  public void copyArea( final int x, final int y, final int width, final int height, final int dx, final int dy ) {

  }

  /**
   * @see Graphics#drawLine(int, int, int, int)
   */
  @Override
  public void drawLine( final int x1, final int y1, final int x2, final int y2 ) {
    final Line2D line = new Line2D.Double( x1, y1, x2, y2 );
    draw( line );
  }

  /**
   * @see Graphics#fillRect(int, int, int, int)
   */
  @Override
  public void drawRect( final int x, final int y, final int width, final int height ) {
    draw( new Rectangle( x, y, width, height ) );
  }

  /**
   * @see Graphics#fillRect(int, int, int, int)
   */
  @Override
  public void fillRect( final int x, final int y, final int width, final int height ) {
    fill( new Rectangle( x, y, width, height ) );
  }

  /**
   * @see Graphics#clearRect(int, int, int, int)
   */
  @Override
  public void clearRect( final int x, final int y, final int width, final int height ) {
    final Paint temp = paint;
    setPaint( background );
    fillRect( x, y, width, height );
    setPaint( temp );
  }

  /**
   * @see Graphics#drawRoundRect(int, int, int, int, int, int)
   */
  @Override
  public void drawRoundRect( final int x, final int y, final int width, final int height, final int arcWidth,
      final int arcHeight ) {
    final RoundRectangle2D rect = new RoundRectangle2D.Double( x, y, width, height, arcWidth, arcHeight );
    draw( rect );
  }

  /**
   * @see Graphics#fillRoundRect(int, int, int, int, int, int)
   */
  @Override
  public void fillRoundRect( final int x, final int y, final int width, final int height, final int arcWidth,
      final int arcHeight ) {
    final RoundRectangle2D rect = new RoundRectangle2D.Double( x, y, width, height, arcWidth, arcHeight );
    fill( rect );
  }

  /**
   * @see Graphics#drawOval(int, int, int, int)
   */
  @Override
  public void drawOval( final int x, final int y, final int width, final int height ) {
    final Ellipse2D oval = new Ellipse2D.Float( x, y, width, height );
    draw( oval );
  }

  /**
   * @see Graphics#fillOval(int, int, int, int)
   */
  @Override
  public void fillOval( final int x, final int y, final int width, final int height ) {
    final Ellipse2D oval = new Ellipse2D.Float( x, y, width, height );
    fill( oval );
  }

  /**
   * @see Graphics#drawArc(int, int, int, int, int, int)
   */
  @Override
  public void drawArc( final int x, final int y, final int width, final int height, final int startAngle,
      final int arcAngle ) {
    final Arc2D arc = new Arc2D.Double( x, y, width, height, startAngle, arcAngle, Arc2D.OPEN );
    draw( arc );

  }

  /**
   * @see Graphics#fillArc(int, int, int, int, int, int)
   */
  @Override
  public void fillArc( final int x, final int y, final int width, final int height, final int startAngle,
      final int arcAngle ) {
    final Arc2D arc = new Arc2D.Double( x, y, width, height, startAngle, arcAngle, Arc2D.PIE );
    fill( arc );
  }

  /**
   * @see Graphics#drawPolyline(int[], int[], int)
   */
  @Override
  public void drawPolyline( final int[] x, final int[] y, final int nPoints ) {
    final Line2D line = new Line2D.Double( x[0], y[0], x[0], y[0] );
    for ( int i = 1; i < nPoints; i++ ) {
      line.setLine( line.getX2(), line.getY2(), x[i], y[i] );
      draw( line );
    }
  }

  /**
   * @see Graphics#drawPolygon(int[], int[], int)
   */
  @Override
  public void drawPolygon( final int[] xPoints, final int[] yPoints, final int nPoints ) {
    final Polygon poly = new Polygon();
    for ( int i = 0; i < nPoints; i++ ) {
      poly.addPoint( xPoints[i], yPoints[i] );
    }
    draw( poly );
  }

  /**
   * @see Graphics#fillPolygon(int[], int[], int)
   */
  @Override
  public void fillPolygon( final int[] xPoints, final int[] yPoints, final int nPoints ) {
    final Polygon poly = new Polygon();
    for ( int i = 0; i < nPoints; i++ ) {
      poly.addPoint( xPoints[i], yPoints[i] );
    }
    fill( poly );
  }

  /**
   * @see Graphics#drawImage(Image, int, int, ImageObserver)
   */
  @Override
  public boolean drawImage( final Image img, final int x, final int y, final ImageObserver observer ) {
    return drawImage( img, x, y, null, observer );
  }

  /**
   * @see Graphics#drawImage(Image, int, int, int, int, ImageObserver)
   */
  @Override
  public boolean drawImage( final Image img, final int x, final int y, final int width, final int height,
      final ImageObserver observer ) {
    return drawImage( img, x, y, width, height, null, observer );
  }

  /**
   * @see Graphics#drawImage(Image, int, int, Color, ImageObserver)
   */
  @Override
  public boolean
    drawImage( final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer ) {
    waitForImage( img );
    return drawImage( img, x, y, img.getWidth( observer ), img.getHeight( observer ), bgcolor, observer );
  }

  /**
   * @see Graphics#drawImage(Image, int, int, int, int, Color, ImageObserver)
   */
  @Override
  public boolean drawImage( final Image img, final int x, final int y, final int width, final int height,
      final Color bgcolor, final ImageObserver observer ) {
    waitForImage( img );
    final double scalex = width / (double) img.getWidth( observer );
    final double scaley = height / (double) img.getHeight( observer );
    final AffineTransform tx = AffineTransform.getTranslateInstance( x, y );
    tx.scale( scalex, scaley );
    return drawImage( img, null, tx, bgcolor, observer );
  }

  /**
   * @see Graphics#drawImage(Image, int, int, int, int, int, int, int, int, ImageObserver)
   */
  @Override
  public boolean drawImage( final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1,
      final int sy1, final int sx2, final int sy2, final ImageObserver observer ) {
    return drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer );
  }

  /**
   * @see Graphics#drawImage(Image, int, int, int, int, int, int, int, int, Color, ImageObserver)
   */
  @Override
  public boolean drawImage( final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1,
      final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer ) {
    waitForImage( img );
    final double dwidth = (double) dx2 - dx1;
    final double dheight = (double) dy2 - dy1;
    final double swidth = (double) sx2 - sx1;
    final double sheight = (double) sy2 - sy1;

    // if either width or height is 0, then there is nothing to draw
    if ( dwidth == 0 || dheight == 0 || swidth == 0 || sheight == 0 ) {
      return true;
    }

    final double scalex = dwidth / swidth;
    final double scaley = dheight / sheight;

    final double transx = sx1 * scalex;
    final double transy = sy1 * scaley;
    final AffineTransform tx = AffineTransform.getTranslateInstance( dx1 - transx, dy1 - transy );
    tx.scale( scalex, scaley );

    final BufferedImage mask =
        new BufferedImage( img.getWidth( observer ), img.getHeight( observer ), BufferedImage.TYPE_BYTE_BINARY );
    final Graphics g = mask.getGraphics();
    g.fillRect( sx1, sy1, (int) swidth, (int) sheight );
    drawImage( img, mask, tx, null, observer );
    g.dispose();
    return true;
  }

  /**
   * @see Graphics#dispose()
   */
  @Override
  public void dispose() {

    if ( !disposeCalled ) {
      disposeCalled = true;
      cb.restoreState();
      cb.restoreState();
      dg2.dispose();
      dg2 = null;
      if ( kids != null ) {
        for ( int k = 0; k < kids.size(); ++k ) {
          final PdfGraphics2D g2 = (PdfGraphics2D) kids.get( k );
          if ( g2.disposeCalled == false ) {
            g2.dispose();
          }
        }
      }

      if ( parent != null ) {
        parent.cb.add( cb );
        parent = null;
      }
    }
  }

  // private void internalDispose(ByteBuffer buf) {
  // int last = 0;
  //
  // final ByteBuffer buf2 = cb.getInternalBuffer();
  //
  // for (int k = 0; k < kids.size(); k += 1) {
  // // final int pos = ((Integer)kids.get(k)).intValue();
  // final PdfGraphics2D g2 = (PdfGraphics2D)kids.get(k + 1);
  // g2.cb.restoreState();
  // g2.cb.restoreState();
  // buf.append(buf2.getBuffer(), last, pos - last);
  // g2.dg2.dispose();
  // g2.dg2 = null;
  // g2.internalDispose(buf);
  // last = pos;
  // }
  //
  // buf.append(buf2.getBuffer(), last, buf2.size() - last);
  // }

  // /////////////////////////////////////////////
  //
  //
  // implementation specific methods
  //
  //

  private void followPath( Shape s, final int drawType ) {
    if ( s == null ) {
      return;
    }
    if ( drawType == PdfGraphics2D.STROKE ) {
      if ( !( stroke instanceof BasicStroke ) ) {
        s = stroke.createStrokedShape( s );
        followPath( s, PdfGraphics2D.FILL );
        return;
      }
    }
    if ( drawType == PdfGraphics2D.STROKE ) {
      setStrokeDiff( stroke, oldStroke );
      oldStroke = stroke;
      setStrokePaint();
    } else if ( drawType == PdfGraphics2D.FILL ) {
      setFillPaint();
    }
    final PathIterator points;
    if ( drawType == PdfGraphics2D.CLIP ) {
      points = s.getPathIterator( PdfGraphics2D.IDENTITY );
    } else {
      points = s.getPathIterator( transform );
    }
    final float[] coords = new float[6];
    int traces = 0;
    while ( !points.isDone() ) {
      ++traces;
      final int segtype = points.currentSegment( coords );
      normalizeY( coords );
      switch ( segtype ) {
        case PathIterator.SEG_CLOSE:
          cb.closePath();
          break;

        case PathIterator.SEG_CUBICTO:
          cb.curveTo( coords[0], coords[1], coords[2], coords[3], coords[4], coords[5] );
          break;

        case PathIterator.SEG_LINETO:
          cb.lineTo( coords[0], coords[1] );
          break;

        case PathIterator.SEG_MOVETO:
          cb.moveTo( coords[0], coords[1] );
          break;

        case PathIterator.SEG_QUADTO:
          cb.curveTo( coords[0], coords[1], coords[2], coords[3] );
          break;
        default:
          throw new IllegalStateException( "Invalid segment type in path" );
      }
      points.next();
    }
    switch ( drawType ) {
      case PdfGraphics2D.FILL:
        if ( traces > 0 ) {
          if ( points.getWindingRule() == PathIterator.WIND_EVEN_ODD ) {
            cb.eoFill();
          } else {
            cb.fill();
          }
        }
        break;
      case PdfGraphics2D.STROKE:
        if ( traces > 0 ) {
          cb.stroke();
        }
        break;
      default: // drawType==CLIP
        if ( traces == 0 ) {
          cb.rectangle( 0, 0, 0, 0 );
        }
        if ( points.getWindingRule() == PathIterator.WIND_EVEN_ODD ) {
          cb.eoClip();
        } else {
          cb.clip();
        }
        cb.newPath();
    }
  }

  private float normalizeY( final float y ) {
    return this.height - y;
  }

  private void normalizeY( final float[] coords ) {
    coords[1] = normalizeY( coords[1] );
    coords[3] = normalizeY( coords[3] );
    coords[5] = normalizeY( coords[5] );
  }

  private AffineTransform normalizeMatrix() {
    final double[] mx = new double[6];
    AffineTransform result = AffineTransform.getTranslateInstance( 0, 0 );
    result.getMatrix( mx );
    mx[3] = -1;
    mx[5] = height;
    result = new AffineTransform( mx );
    result.concatenate( transform );
    return result;
  }

  private boolean drawImage( final Image img, final Image mask, final AffineTransform xform, final Color bgColor,
      final ImageObserver obs ) {
    try {
      final com.lowagie.text.Image image = com.lowagie.text.Image.getInstance( img, bgColor );
      if ( mask != null ) {
        final com.lowagie.text.Image msk = com.lowagie.text.Image.getInstance( mask, null, true );
        msk.makeMask();
        msk.setInverted( true );
        image.setImageMask( msk );
      }
      return drawPdfImage( image, img, xform, obs );
    } catch ( Exception e ) {
      PdfGraphics2D.logger.error( "Failed to draw the image: ", e );

    }
    return true;

  }

  public boolean drawPdfImage( final com.lowagie.text.Image image, final Image img, AffineTransform xform,
      final ImageObserver obs ) {
    if ( img == null ) {
      throw new NullPointerException( "Image must not be null." );
    }
    if ( image == null ) {
      throw new NullPointerException( "Image must not be null." );
    }
    if ( xform == null ) {
      xform = AffineTransform.getTranslateInstance( 0, 0 );
    }

    xform.translate( 0, img.getHeight( obs ) );
    xform.scale( img.getWidth( obs ), img.getHeight( obs ) );

    final AffineTransform inverse = this.normalizeMatrix();
    final AffineTransform flipper = FLIP_TRANSFORM;
    inverse.concatenate( xform );
    inverse.concatenate( flipper );

    try {
      final double[] mx = new double[6];
      inverse.getMatrix( mx );
      if ( currentFillGState != 255 ) {
        PdfGState gs = fillGState[255];
        if ( gs == null ) {
          gs = new PdfGState();
          gs.setFillOpacity( 1 );
          fillGState[255] = gs;
        }
        cb.setGState( gs );
      }

      cb.addImage( image, (float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4], (float) mx[5] );
    } catch ( Exception ex ) {
      PdfGraphics2D.logger.error( "Failed to draw the image: ", ex );
      // throw new IllegalArgumentException("Failed to draw the image");
    } finally {
      if ( currentFillGState != 255 ) {
        final PdfGState gs = fillGState[currentFillGState];
        cb.setGState( gs );
      }
    }
    return true;
  }

  private boolean checkNewPaint( final Paint oldPaint ) {
    if ( paint == oldPaint ) {
      return false;
    }
    return !( ( paint instanceof Color ) && paint.equals( oldPaint ) );
  }

  public void setFillPaint() {
    if ( checkNewPaint( paintFill ) ) {
      paintFill = paint;
      setPaint( false, 0, 0, true );
    }
  }

  public void setStrokePaint() {
    if ( checkNewPaint( paintStroke ) ) {
      paintStroke = paint;
      setPaint( false, 0, 0, false );
    }
  }

  private void setPaint( final boolean invert, final double xoffset, final double yoffset, final boolean fill ) {
    if ( paint instanceof Color ) {
      final Color color = (Color) paint;
      final int alpha = color.getAlpha();
      if ( fill ) {
        if ( alpha != currentFillGState ) {
          currentFillGState = alpha;
          PdfGState gs = fillGState[alpha];
          if ( gs == null ) {
            gs = new PdfGState();
            gs.setFillOpacity( alpha / 255.00f );
            fillGState[alpha] = gs;
          }
          cb.setGState( gs );
        }
        cb.setColorFill( color );
      } else {
        if ( alpha != currentStrokeGState ) {
          currentStrokeGState = alpha;
          PdfGState gs = strokeGState[alpha];
          if ( gs == null ) {
            gs = new PdfGState();
            gs.setStrokeOpacity( alpha / 255.0f );
            strokeGState[alpha] = gs;
          }
          cb.setGState( gs );
        }
        cb.setColorStroke( color );
      }
    } else if ( paint instanceof GradientPaint ) {
      final GradientPaint gp = (GradientPaint) paint;
      final Point2D p1 = gp.getPoint1();
      transform.transform( p1, p1 );
      final Point2D p2 = gp.getPoint2();
      transform.transform( p2, p2 );
      final Color c1 = gp.getColor1();
      final Color c2 = gp.getColor2();
      final PdfShading shading =
          PdfShading.simpleAxial( cb.getPdfWriter(), (float) p1.getX(), normalizeY( (float) p1.getY() ), (float) p2
              .getX(), normalizeY( (float) p2.getY() ), c1, c2 );
      final PdfShadingPattern pat = new PdfShadingPattern( shading );
      if ( fill ) {
        cb.setShadingFill( pat );
      } else {
        cb.setShadingStroke( pat );
      }
    } else if ( paint instanceof TexturePaint ) {
      try {
        final TexturePaint tp = (TexturePaint) paint;
        final BufferedImage img = tp.getImage();
        final Rectangle2D rect = tp.getAnchorRect();
        final com.lowagie.text.Image image = com.lowagie.text.Image.getInstance( img, null );
        final PdfPatternPainter pattern = cb.createPattern( image.getWidth(), image.getHeight() );
        final AffineTransform inverse = this.normalizeMatrix();
        inverse.translate( rect.getX(), rect.getY() );
        inverse.scale( rect.getWidth() / image.getWidth(), -rect.getHeight() / image.getHeight() );
        final double[] mx = new double[6];
        inverse.getMatrix( mx );
        pattern.setPatternMatrix( (float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4],
            (float) mx[5] );
        image.setAbsolutePosition( 0, 0 );
        pattern.addImage( image );
        if ( fill ) {
          cb.setPatternFill( pattern );
        } else {
          cb.setPatternStroke( pattern );
        }

      } catch ( Exception ex ) {
        if ( fill ) {
          cb.setColorFill( Color.gray );
        } else {
          cb.setColorStroke( Color.gray );
        }
      }
    } else {
      try {
        int type = BufferedImage.TYPE_4BYTE_ABGR;
        if ( paint.getTransparency() == Transparency.OPAQUE ) {
          type = BufferedImage.TYPE_3BYTE_BGR;
        }
        final BufferedImage img = new BufferedImage( (int) width, (int) height, type );
        final Graphics2D g = (Graphics2D) img.getGraphics();
        g.transform( transform );
        final AffineTransform inv = transform.createInverse();
        Shape fillRect = new Rectangle2D.Double( 0, 0, img.getWidth(), img.getHeight() );
        fillRect = inv.createTransformedShape( fillRect );
        g.setPaint( paint );
        g.fill( fillRect );
        if ( invert ) {
          final AffineTransform tx = new AffineTransform();
          tx.scale( 1, -1 );
          tx.translate( -xoffset, -yoffset );
          g.drawImage( img, tx, null );
        }
        g.dispose();
        // g = null;
        final com.lowagie.text.Image image = com.lowagie.text.Image.getInstance( img, null );
        final PdfPatternPainter pattern = cb.createPattern( width, height );
        image.setAbsolutePosition( 0, 0 );
        pattern.addImage( image );
        if ( fill ) {
          cb.setPatternFill( pattern );
        } else {
          cb.setPatternStroke( pattern );
        }
      } catch ( Exception ex ) {
        if ( fill ) {
          cb.setColorFill( Color.gray );
        } else {
          cb.setColorStroke( Color.gray );
        }
      }
    }
  }

  private synchronized void waitForImage( final java.awt.Image image ) {
    if ( mediaTracker == null ) {
      mediaTracker = new MediaTracker( new FakeComponent() );
    }
    mediaTracker.addImage( image, 0 );
    try {
      mediaTracker.waitForID( 0 );
    } catch ( InterruptedException e ) {
      // empty on purpose
    }
    mediaTracker.removeImage( image );
  }

  private static class FakeComponent extends Component {
    private FakeComponent() {
    }

    private static final long serialVersionUID = 6450197945596086638L;
  }
}
