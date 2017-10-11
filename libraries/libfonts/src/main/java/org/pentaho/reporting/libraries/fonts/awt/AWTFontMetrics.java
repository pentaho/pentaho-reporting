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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.awt;

import org.pentaho.reporting.libraries.fonts.LibFontsDefaults;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointUtilities;
import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Creation-Date: 16.12.2005, 21:09:39
 *
 * @author Thomas Morgner
 */
public class AWTFontMetrics implements FontMetrics {
  private static final Graphics2D[] graphics = new Graphics2D[ 4 ];

  static {
    graphics[ 0 ] = produce( false, false );
    graphics[ 1 ] = produce( true, false );
    graphics[ 2 ] = produce( false, true );
    graphics[ 3 ] = produce( true, true );
  }

  private static Graphics2D produce( boolean antiAlias, boolean fractMetrics ) {
    final BufferedImage image = new BufferedImage
      ( 1, 1, BufferedImage.TYPE_INT_ARGB );
    final Graphics2D g2 = image.createGraphics();
    if ( antiAlias ) {
      g2.setRenderingHint
        ( RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
    } else {
      g2.setRenderingHint
        ( RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
    }
    if ( fractMetrics ) {
      g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON );
    } else {
      g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
    }
    return g2;
  }

  private FontNativeContext record;
  private Font font;
  private long maxCharAdvance;
  private char[] cpBuffer;
  private FontRenderContext frc;
  private long xheight;
  private long ascent;
  private long descent;

  private long[] cachedWidths;
  private BaselineInfo[] cachedBaselines;
  private long leading;
  private long maxAscent;
  private long maxDescent;
  private boolean uniformLineMetrics;

  public AWTFontMetrics( final FontNativeContext record, final Font font, final FontContext context ) {
    this.record = record;
    this.font = font;
    this.frc = new FontRenderContext( null, context.isAntiAliased(), context.isFractionalMetrics() );

    final Graphics2D graphics = createGraphics( context );
    // This is some sort of evil hack and does only play well as long as we deal with scalable fonts
    // It will happily cause troubles with bitmap fonts.
    java.awt.FontMetrics fontMetrics = graphics.getFontMetrics( font.deriveFont( font.getSize2D() * 1000 ) );
    this.leading = fontMetrics.getLeading();
    this.maxAscent = fontMetrics.getMaxAscent();
    this.maxDescent = fontMetrics.getMaxDescent();
    this.uniformLineMetrics = fontMetrics.hasUniformLineMetrics();

    final Rectangle2D rect = this.font.getMaxCharBounds( frc );
    this.maxCharAdvance = FontStrictGeomUtility.toInternalValue( rect.getWidth() );
    this.ascent = FontStrictGeomUtility.toInternalValue( -rect.getY() );
    this.descent = FontStrictGeomUtility.toInternalValue( rect.getHeight() + rect.getY() );

    final GlyphVector gv = font.createGlyphVector( frc, "x" );
    final Rectangle2D bounds = gv.getVisualBounds();
    this.xheight = FontStrictGeomUtility.toInternalValue( bounds.getHeight() );

    this.cpBuffer = new char[ 4 ];
    this.cachedBaselines = new BaselineInfo[ 256 - 32 ];
    this.cachedWidths = new long[ 256 - 32 ];
    Arrays.fill( cachedWidths, -1 );
  }

  protected Graphics2D createGraphics( final FontContext context ) {
    int idx = 0;
    if ( context.isAntiAliased() ) {
      idx += 1;
    }
    if ( context.isFractionalMetrics() ) {
      idx += 2;
    }

    return graphics[ idx ];
  }

  public Font getFont() {
    return font;
  }

  /**
   * From the baseline to the
   *
   * @return
   */
  public long getAscent() {
    return ascent;
  }

  public long getDescent() {
    return descent;
  }

  public long getLeading() {
    return leading; //FontStrictGeomUtility.toInternalValue(fontMetrics.getLeading());
  }

  /**
   * The height of the lowercase 'x'. This is used as hint, which size the lowercase characters will have.
   *
   * @return
   */
  public long getXHeight() {
    return xheight;
  }

  public long getOverlinePosition() {
    return getLeading() - Math.max( 1000, getMaxHeight() / 20 );
  }

  public long getUnderlinePosition() {
    return getLeading() + getMaxAscent() + Math.max( 1000, getMaxHeight() / 20 );
  }

  public long getStrikeThroughPosition() {
    return getMaxAscent() - (long) ( LibFontsDefaults.DEFAULT_STRIKETHROUGH_POSITION * getXHeight() );
  }

  public long getMaxAscent() {
    return maxAscent; //FontStrictGeomUtility.toInternalValue(fontMetrics.getMaxAscent());
  }

  public long getMaxDescent() {
    return maxDescent; //FontStrictGeomUtility.toInternalValue(fontMetrics.getMaxDescent());
  }

  public long getMaxHeight() {
    return getMaxAscent() + getMaxDescent() + getLeading();
  }

  public long getMaxCharAdvance() {
    return maxCharAdvance;
  }

  public long getCharWidth( final int character ) {
    if ( character >= 32 && character < 256 ) {
      // can be cached ..
      final int index = character - 32;
      final long cachedWidth = cachedWidths[ index ];
      if ( cachedWidth >= 0 ) {
        return cachedWidth;
      }

      final int retval = CodePointUtilities.toChars( character, cpBuffer, 0 );

      if ( retval > 0 ) {
        final Rectangle2D lm = font.getStringBounds( cpBuffer, 0, retval, frc );
        final long width = FontStrictGeomUtility.toInternalValue( lm.getWidth() );
        cachedWidths[ index ] = width;
        return width;
      } else {
        cachedWidths[ index ] = 0;
        return 0;
      }
    }

    final int retval = CodePointUtilities.toChars( character, cpBuffer, 0 );

    if ( retval > 0 ) {
      final Rectangle2D lm = font.getStringBounds( cpBuffer, 0, retval, frc );
      return FontStrictGeomUtility.toInternalValue( lm.getWidth() );
    } else {
      return 0;
    }
  }

  /**
   * This method is <b>EXPENSIVE</b>.
   *
   * @param previous
   * @param character
   * @return
   */
  public long getKerning( final int previous, final int character ) {
    final int retvalC1 = CodePointUtilities.toChars( previous, cpBuffer, 0 );
    if ( retvalC1 <= 0 ) {
      return 0;
    }

    final int retvalC2 = CodePointUtilities.toChars( character, cpBuffer, retvalC1 );
    if ( retvalC2 > 0 ) {
      final int limit = ( retvalC1 + retvalC2 );
      final GlyphVector gv = font.createGlyphVector( frc, new String( cpBuffer, 0, limit ) );
      final long totalSize = FontStrictGeomUtility.toInternalValue( gv.getGlyphPosition( limit ).getX() );
      final long renderedWidth = FontStrictGeomUtility.toInternalValue( gv.getOutline().getBounds2D().getWidth() );
      return totalSize - renderedWidth;
    } else {
      return 0;
    }
  }

  /**
   * Baselines are defined for scripts, not glyphs. A glyph carries script information most of the time (unless it is a
   * neutral characters or just weird).
   * <p/>
   * The baseline info does not take any leading into account.
   *
   * @param c the character that is used to select the script type.
   * @return
   */
  public BaselineInfo getBaselines( final int c, BaselineInfo info ) {
    final boolean cacheable = ( c >= 32 && c < 256 );
    if ( cacheable ) {
      final BaselineInfo fromCache = cachedBaselines[ c - 32 ];
      if ( fromCache != null ) {
        if ( info == null ) {
          info = new BaselineInfo();
        }
        info.update( fromCache );
        return info;
      }
    }

    cpBuffer[ 0 ] = (char) ( c & 0xFFFF );
    final LineMetrics lm = font.getLineMetrics( cpBuffer, 0, 1, frc );
    final float[] bls = lm.getBaselineOffsets();
    final int idx = lm.getBaselineIndex();

    if ( info == null ) {
      info = new BaselineInfo();
    }

    // The ascent is local - but we need the global baseline, relative to the
    // MaxAscent.
    final long maxAscent = getMaxAscent();
    final long ascent = FontStrictGeomUtility.toInternalValue( lm.getAscent() );
    final long delta = maxAscent - ascent;
    info.setBaseline( BaselineInfo.MATHEMATICAL, delta + maxAscent - getXHeight() );
    info.setBaseline( BaselineInfo.IDEOGRAPHIC, getMaxHeight() );
    info.setBaseline( BaselineInfo.MIDDLE, maxAscent / 2 );

    final long base = delta + ascent;

    switch( idx ) {
      case Font.CENTER_BASELINE: {
        info.setBaseline( BaselineInfo.CENTRAL, base );
        info.setBaseline( BaselineInfo.ALPHABETIC,
          base + FontStrictGeomUtility.toInternalValue( bls[ Font.ROMAN_BASELINE ] ) );
        info.setBaseline( BaselineInfo.HANGING,
          base + FontStrictGeomUtility.toInternalValue( bls[ Font.HANGING_BASELINE ] ) );
        info.setDominantBaseline( BaselineInfo.CENTRAL );
        break;
      }
      case Font.HANGING_BASELINE: {
        info.setBaseline( BaselineInfo.CENTRAL,
          base + FontStrictGeomUtility.toInternalValue( bls[ Font.CENTER_BASELINE ] ) );
        info.setBaseline( BaselineInfo.ALPHABETIC,
          base + FontStrictGeomUtility.toInternalValue( bls[ Font.ROMAN_BASELINE ] ) );
        info.setBaseline( BaselineInfo.HANGING, base );
        info.setDominantBaseline( BaselineInfo.HANGING );
        break;
      }
      default: // ROMAN Base-line
      {
        info.setBaseline( BaselineInfo.ALPHABETIC, base );
        info.setBaseline( BaselineInfo.CENTRAL,
          base + FontStrictGeomUtility.toInternalValue( bls[ Font.CENTER_BASELINE ] ) );
        info.setBaseline( BaselineInfo.HANGING,
          base + FontStrictGeomUtility.toInternalValue( bls[ Font.HANGING_BASELINE ] ) );
        info.setDominantBaseline( BaselineInfo.ALPHABETIC );
        break;
      }
    }

    if ( cacheable ) {
      final BaselineInfo cached = new BaselineInfo();
      cached.update( info );
      cachedBaselines[ c - 32 ] = cached;
    }

    return info;
  }

  /**
   * Is it guaranteed that the font always returns the same baseline info objct?
   *
   * @return true, if the baseline info in question is always the same, false otherwise.
   */
  public boolean isUniformFontMetrics() {
    return uniformLineMetrics;
  }

  /**
   * Returns zero, as the AWT renderer will take care of the italic rendering already. We do not have to apply any
   * special transformations to the font to make it look italic.
   *
   * @return always zero.
   */
  public long getItalicAngle() {
    return 0;
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
