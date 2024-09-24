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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class RotatedTextDrawable implements ReportDrawable {

  public RotatedTextDrawable( final String text, final TextRotation rotation ) {
    this.text = text;
    this.rotation = rotation;
  }

  private String text;
  private final TextRotation rotation;
  private Font font;
  private ElementAlignment hAlign;
  private ElementAlignment vAlign;


  @Override public void draw( final Graphics2D graphics2D, final Rectangle2D bounds ) {

    boolean fromBottom = TextRotation.D_90.equals( rotation );

    if ( font != null ) {
      graphics2D.setFont( font );
    }

    final FontMetrics fontMetrics = graphics2D.getFontMetrics();
    drawTransformed( graphics2D, getXPadding( fromBottom, fontMetrics, bounds ),
      getYPadding( fromBottom, fontMetrics, bounds ),
      fromBottom ? 3 * java.lang.Math.PI / 2 : java.lang.Math.PI / 2 );
  }


  private int getXPadding( final boolean fromBottom, final FontMetrics fontMetrics, final Rectangle2D bounds ) {

    if ( ElementAlignment.CENTER.equals( hAlign ) ) {
      return (int) ( bounds.getWidth() / 2 );
    }
    if ( ElementAlignment.LEFT.equals( hAlign ) || ElementAlignment.JUSTIFY.equals( hAlign ) ) {
      return fromBottom ? fontMetrics.getAscent() : fontMetrics.getDescent();
    }
    if ( ElementAlignment.RIGHT.equals( hAlign ) ) {
      return (int) ( bounds.getWidth() - ( fromBottom ? fontMetrics.getDescent() : fontMetrics.getAscent() ) );
    }

    return 0;
  }

  private int getYPadding( final boolean fromBottom, final FontMetrics fontMetrics, final Rectangle2D bounds ) {

    final int stringWidth = fontMetrics.stringWidth( text );

    if ( ElementAlignment.TOP.equals( vAlign ) ) {
      return fromBottom ? stringWidth : 0;
    }
    if ( ElementAlignment.MIDDLE.equals( vAlign ) ) {
      return (int) ( ( bounds.getHeight() + ( fromBottom ? 1 : -1 ) * stringWidth ) / 2 );
    }
    if ( ElementAlignment.BOTTOM.equals( vAlign ) ) {
      return fromBottom ? (int) bounds.getHeight() : (int) ( bounds.getHeight() - stringWidth );
    }

    return 0;
  }

  private void drawTransformed( final Graphics2D g2D, final double x, final double y, final double theta ) {
    AffineTransform fontAT = new AffineTransform();
    Font theFont = g2D.getFont();
    fontAT.rotate( theta );
    Font theDerivedFont = theFont.deriveFont( fontAT );
    g2D.setFont( theDerivedFont );
    g2D.drawString( text, (int) x, (int) y );
    g2D.setFont( theFont );
  }


  public Dimension getPreferredSize() {
    BufferedImage bufferedImage = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
    Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
    graphics.setFont( font );
    final FontMetrics fontMetrics = graphics.getFontMetrics();
    return new Dimension( fontMetrics.getHeight(), fontMetrics.stringWidth( text ) );
  }

  public boolean isKeepAspectRatio() {
    return true;
  }


  @Override public void setConfiguration( final Configuration config ) {

  }

  @Override public void setStyleSheet( final StyleSheet style ) {
    if ( style != null ) {
      final String fontName = (String) style.getStyleProperty( TextStyleKeys.FONT );
      final int fontSize = style.getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
      final boolean bold = style.getBooleanStyleProperty( TextStyleKeys.BOLD );
      final boolean italics = style.getBooleanStyleProperty( TextStyleKeys.ITALIC );
      final boolean underlined = style.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
      final boolean strikeThrough = style.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );
      final Color foregroundColor = (Color) style.getStyleProperty( ElementStyleKeys.PAINT );
      final Color backgroundColor = (Color) style.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
      if ( fontName != null && fontSize > 0 ) {
        int fontstyle = Font.PLAIN;
        if ( bold ) {
          fontstyle |= Font.BOLD;
        }
        if ( italics ) {
          fontstyle |= Font.ITALIC;
        }

        final Map<TextAttribute, Object> fontAttributes = new HashMap<>();
        if ( underlined ) {
          fontAttributes.put( TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON );
        }
        if ( strikeThrough ) {
          fontAttributes.put( TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON );
        }

        fontAttributes.put( TextAttribute.FOREGROUND, foregroundColor );
        fontAttributes.put( TextAttribute.BACKGROUND, backgroundColor );

        this.font = new Font( fontName, fontstyle, fontSize ).deriveFont( fontAttributes );
        this.hAlign = (ElementAlignment) style.getStyleProperty( ElementStyleKeys.ALIGNMENT );
        this.vAlign = (ElementAlignment) style.getStyleProperty( ElementStyleKeys.VALIGNMENT );
      }
    }
  }

  @Override public void setResourceBundleFactory( final ResourceBundleFactory bundleFactory ) {

  }

  @Override public ImageMap getImageMap( final Rectangle2D bounds ) {
    return null;
  }

  @Override public String toString() {
    return text;
  }

  public String getText() {
    return text;
  }

  public TextRotation getRotation() {
    return rotation;
  }

  public static RotatedTextDrawable extract( final Object value ) {
    if ( value instanceof RotatedTextDrawable ) {
      return (RotatedTextDrawable) value;
    }
    if ( value != null && value instanceof DrawableWrapper ) {
      if ( ( (DrawableWrapper) value ).getBackend() instanceof RotatedTextDrawable ) {
        return (RotatedTextDrawable) ( (DrawableWrapper) value ).getBackend();
      }
    }
    return null;
  }

  public ElementAlignment gethAlign() {
    return hAlign;
  }

  public ElementAlignment getvAlign() {
    return vAlign;
  }
}
