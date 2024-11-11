/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.registry;

import org.pentaho.reporting.libraries.fonts.LibFontsDefaults;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * An placeholder metrics for buggy fonts.
 *
 * @author Thomas Morgner
 */
public class EmptyFontMetrics implements FontMetrics {
  private long baseSize;
  private long baseWidth;
  private FontNativeContext record;

  public EmptyFontMetrics( final FontNativeContext record, final double baseWidth, final double baseHeight ) {
    this.record = record;
    this.baseSize = FontStrictGeomUtility.toInternalValue( baseHeight );
    this.baseWidth = FontStrictGeomUtility.toInternalValue( baseWidth );
  }

  public EmptyFontMetrics( final FontNativeContext record, final long baseWidth, final long baseHeight ) {
    this.record = record;
    this.baseWidth = baseWidth;
    this.baseSize = baseHeight;
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
    return FontStrictGeomUtility.toInternalValue( baseSize * LibFontsDefaults.DEFAULT_ASCENT_SIZE );
  }

  public long getDescent() {
    return FontStrictGeomUtility.toInternalValue( baseSize * LibFontsDefaults.DEFAULT_DESCENT_SIZE );
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
    return FontStrictGeomUtility.toInternalValue( baseSize * LibFontsDefaults.DEFAULT_XHEIGHT_SIZE );
  }

  public long getOverlinePosition() {
    return getLeading() - Math.max( 1000, baseSize / 20 );
  }

  public long getUnderlinePosition() {
    return getAscent() + Math.max( 1000, baseSize / 20 );
  }

  public long getStrikeThroughPosition() {
    return FontStrictGeomUtility.toInternalValue( getXHeight() * LibFontsDefaults.DEFAULT_STRIKETHROUGH_POSITION );
  }

  public long getMaxAscent() {
    return getAscent();
  }

  public long getMaxDescent() {
    return getDescent();
  }

  public long getMaxHeight() {
    return baseSize;
  }

  public long getMaxCharAdvance() {
    return baseWidth;
  }

  public long getCharWidth( final int codePoint ) {
    return baseWidth;
  }

  public long getKerning( final int previous, final int codePoint ) {
    return 0;
  }

  /**
   * Baselines are defined for scripts, not glyphs. A glyph carries script information most of the time (unless it is a
   * neutral characters or just weird).
   *
   * @param c
   * @return
   */
  public BaselineInfo getBaselines( final int c, BaselineInfo info ) {
    if ( info == null ) {
      info = new BaselineInfo();
    }

    // this is the most dilletantic baseline computation on this planet.
    // But without any font metrics, it is also the best baseline computation :)

    // The ascent is local - but we need the global baseline, relative to the
    // MaxAscent.
    final long maxAscent = getMaxAscent();
    info.setBaseline( BaselineInfo.MATHEMATICAL, maxAscent - getXHeight() );
    info.setBaseline( BaselineInfo.IDEOGRAPHIC, getMaxHeight() );
    info.setBaseline( BaselineInfo.MIDDLE, maxAscent / 2 );
    info.setBaseline( BaselineInfo.ALPHABETIC, maxAscent );
    info.setBaseline( BaselineInfo.CENTRAL, maxAscent / 2 );
    info.setBaseline( BaselineInfo.HANGING, maxAscent - getXHeight() );
    info.setDominantBaseline( BaselineInfo.ALPHABETIC );
    return info;
  }

  public long getItalicAngle() {
    return 0;
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
