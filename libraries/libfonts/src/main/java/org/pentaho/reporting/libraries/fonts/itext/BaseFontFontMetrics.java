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

package org.pentaho.reporting.libraries.fonts.itext;

import com.lowagie.text.pdf.BaseFont;
import org.pentaho.reporting.libraries.fonts.LibFontsDefaults;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointUtilities;
import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

import java.util.Arrays;

/**
 * Creation-Date: 22.07.2007, 19:04:00
 *
 * @author Thomas Morgner
 */
public class BaseFontFontMetrics implements FontMetrics {
  private BaseFont baseFont;
  private float size;
  private long xHeight;
  private char[] cpBuffer;
  private long[] cachedWidths;
  private long ascent;
  private long descent;
  private long leading;
  private long sizeScaled;
  private long italicsAngle;
  private long maxAscent;
  private long maxDescent;
  private long maxCharAdvance;
  private boolean trueTypeFont;
  private transient BaselineInfo cachedBaselineInfo;
  private FontNativeContext record;

  public BaseFontFontMetrics( final FontNativeContext record, final BaseFont baseFont, final float size ) {
    if ( baseFont == null ) {
      throw new NullPointerException( "BaseFont is invalid." );
    }

    this.record = record;
    this.baseFont = baseFont;
    this.size = size;
    this.cpBuffer = new char[ 4 ];
    this.cachedWidths = new long[ 256 - 32 ];
    Arrays.fill( cachedWidths, -1 );

    sizeScaled = FontStrictGeomUtility.toInternalValue( size );

    this.ascent = (long) baseFont.getFontDescriptor( BaseFont.AWT_ASCENT, sizeScaled );
    this.descent = (long) -baseFont.getFontDescriptor( BaseFont.AWT_DESCENT, sizeScaled );
    this.leading = (long) baseFont.getFontDescriptor( BaseFont.AWT_LEADING, sizeScaled );
    italicsAngle = FontStrictGeomUtility.toInternalValue( baseFont.getFontDescriptor( BaseFont.ITALICANGLE, size ) );
    maxAscent = (long) baseFont.getFontDescriptor( BaseFont.BBOXURY, sizeScaled );
    maxDescent = (long) -baseFont.getFontDescriptor( BaseFont.BBOXLLY, sizeScaled );
    maxCharAdvance = (long) baseFont.getFontDescriptor( BaseFont.AWT_MAXADVANCE, sizeScaled );

    final int[] charBBox = this.baseFont.getCharBBox( 'x' );
    if ( charBBox != null ) {
      this.xHeight = (long) ( charBBox[ 3 ] * size );
    }
    if ( this.xHeight == 0 ) {
      this.xHeight = getAscent() / 2;
    }
    this.trueTypeFont = baseFont.getFontType() == BaseFont.FONT_TYPE_TT ||
      baseFont.getFontType() == BaseFont.FONT_TYPE_TTUNI;
  }

  public boolean isTrueTypeFont() {
    return trueTypeFont;
  }

  public long getAscent() {
    return ascent;
  }

  public long getDescent() {
    return descent;
  }

  public long getLeading() {
    return leading;
  }

  public long getXHeight() {
    return xHeight;
  }

  public long getOverlinePosition() {
    return getLeading() - Math.max( 1000, sizeScaled / 20 );
  }

  public long getUnderlinePosition() {
    return ( getLeading() + getMaxAscent() ) + Math.max( 1000, sizeScaled / 20 );
  }

  public long getStrikeThroughPosition() {
    return getMaxAscent() - (long) ( LibFontsDefaults.DEFAULT_STRIKETHROUGH_POSITION * getXHeight() );
  }

  public long getItalicAngle() {
    return italicsAngle;
  }

  public long getMaxAscent() {
    return maxAscent;
  }

  public long getMaxDescent() {
    return maxDescent;
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

      if ( retval == 1 ) {
        final char char1 = cpBuffer[ 0 ];
        if ( char1 < 128 || ( char1 >= 160 && char1 <= 255 ) ) {
          final long width = (long) ( baseFont.getWidth( char1 ) * size );
          cachedWidths[ index ] = width;
          return width;
        }
      } else if ( retval < 1 ) {
        cachedWidths[ index ] = 0;
        return 0;
      }

      final long width = (long) ( baseFont.getWidth( new String( cpBuffer, 0, retval ) ) * size );
      cachedWidths[ index ] = width;
      return width;
    }

    final int retval = CodePointUtilities.toChars( character, cpBuffer, 0 );
    if ( retval == 1 ) {
      final char char1 = cpBuffer[ 0 ];
      if ( char1 < 128 || ( char1 >= 160 && char1 <= 255 ) ) {
        return (long) ( baseFont.getWidth( char1 ) * size );
      }
    } else if ( retval < 1 ) {
      return 0;
    }

    return (long) ( baseFont.getWidth( new String( cpBuffer, 0, retval ) ) * size );
  }

  public long getKerning( final int previous, final int codePoint ) {
    return (long) ( size * baseFont.getKerning( (char) previous, (char) codePoint ) );
  }

  /**
   * Is it guaranteed that the font always returns the same baseline info objct?
   *
   * @return true, if the baseline info in question is always the same, false otherwise.
   */
  public boolean isUniformFontMetrics() {
    return true;
  }

  public BaselineInfo getBaselines( final int c, BaselineInfo info ) {
    if ( cachedBaselineInfo != null ) {
      if ( info == null ) {
        info = new BaselineInfo();
      }
      info.update( cachedBaselineInfo );
      return info;
    }

    if ( info == null ) {
      info = new BaselineInfo();
    }

    // If we had more data, we could surely create something better. Well, this has to be enough ..
    final long maxAscent = getMaxAscent();
    info.setBaseline( BaselineInfo.MATHEMATICAL, maxAscent - getXHeight() );
    info.setBaseline( BaselineInfo.IDEOGRAPHIC, getMaxHeight() );
    info.setBaseline( BaselineInfo.MIDDLE, maxAscent / 2 );
    info.setBaseline( BaselineInfo.ALPHABETIC, maxAscent );
    info.setBaseline( BaselineInfo.CENTRAL, maxAscent / 2 );
    info.setBaseline( BaselineInfo.HANGING, maxAscent - getXHeight() );
    info.setDominantBaseline( BaselineInfo.ALPHABETIC );

    final BaselineInfo cached = new BaselineInfo();
    cached.update( info );
    cachedBaselineInfo = cached;
    return info;
  }

  public BaseFont getBaseFont() {
    return baseFont;
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
