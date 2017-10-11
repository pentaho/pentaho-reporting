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

package org.pentaho.reporting.libraries.fonts.monospace;

import org.pentaho.reporting.libraries.fonts.LibFontsDefaults;
import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;

/**
 * Creation-Date: 13.05.2007, 13:14:25
 *
 * @author Thomas Morgner
 */
public class MonospaceFontMetrics implements FontMetrics {
  private static final long MICRO_DOTS_PER_INCH = 72000;
  private long charHeight;
  private long charWidth;
  private FontNativeContext record;

  public MonospaceFontMetrics( final FontNativeContext record, final float cpi, final float lpi ) {
    this.record = record;
    charHeight = (long) ( MICRO_DOTS_PER_INCH / lpi );
    charWidth = (long) ( MICRO_DOTS_PER_INCH / cpi );
  }

  /**
   * Is it guaranteed that the font always returns the same baseline info objct?
   *
   * @return true, if the baseline info in question is always the same, false otherwise.
   */
  public boolean isUniformFontMetrics() {
    return true;
  }

  /**
   * From the baseline to the
   *
   * @return
   */
  public long getAscent() {
    return (long) ( LibFontsDefaults.DEFAULT_ASCENT_SIZE * charHeight );
  }

  public long getDescent() {
    return (long) ( LibFontsDefaults.DEFAULT_DESCENT_SIZE * charHeight );
  }

  public long getLeading() {
    return 0;
  }

  /**
   * The height of the lowercase 'x'. This is used as hint, which size the lowercase characters will have.
   *
   * @return
   */
  public long getXHeight() {
    return (long) ( LibFontsDefaults.DEFAULT_XHEIGHT_SIZE * charHeight );
  }

  public long getOverlinePosition() {
    return getLeading() - Math.max( 1000, charHeight / 20 );
  }

  public long getUnderlinePosition() {
    return getAscent() + Math.max( 1000, charHeight / 20 );
  }

  public long getStrikeThroughPosition() {
    return (long) ( LibFontsDefaults.DEFAULT_STRIKETHROUGH_POSITION * getXHeight() );
  }

  public long getMaxAscent() {
    return getAscent();
  }

  public long getMaxDescent() {
    return getDescent();
  }

  public long getMaxHeight() {
    return charHeight;
  }

  public long getMaxCharAdvance() {
    return charWidth;
  }

  public long getCharWidth( final int codePoint ) {
    return charWidth;
  }

  public long getKerning( final int previous, final int codePoint ) {
    return 0;
  }

  public long getItalicAngle() {
    return 0;
  }

  /**
   * Baselines are defined for scripts, not glyphs. A glyph carries script information most of the time (unless it is a
   * neutral characters or just weird).
   *
   * @param codePoint
   * @return
   */
  public BaselineInfo getBaselines( final int codePoint, BaselineInfo info ) {
    if ( info == null ) {
      info = new BaselineInfo();
    }


    info.setBaseline( BaselineInfo.HANGING, 0 );
    info.setBaseline( BaselineInfo.MATHEMATICAL, charHeight / 2 );
    info.setBaseline( BaselineInfo.CENTRAL, charHeight / 2 );
    info.setBaseline( BaselineInfo.MIDDLE, charHeight / 2 );
    info.setBaseline( BaselineInfo.ALPHABETIC, getMaxAscent() );
    info.setBaseline( BaselineInfo.IDEOGRAPHIC, getMaxHeight() );
    return info;
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
