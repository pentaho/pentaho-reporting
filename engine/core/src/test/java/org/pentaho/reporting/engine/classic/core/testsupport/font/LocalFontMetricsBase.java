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


package org.pentaho.reporting.engine.classic.core.testsupport.font;

import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;

import java.io.Serializable;
import java.util.HashMap;

public class LocalFontMetricsBase implements Serializable {
  private static class KerningKey implements Serializable {
    private int prev;
    private int codepoint;

    private KerningKey( final int prev, final int codepoint ) {
      this.prev = prev;
      this.codepoint = codepoint;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final KerningKey that = (KerningKey) o;

      if ( codepoint != that.codepoint ) {
        return false;
      }
      if ( prev != that.prev ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = prev;
      result = 31 * result + codepoint;
      return result;
    }
  }

  private int[] charWidth;
  private HashMap<KerningKey, Integer> kerningMap;

  private long ascent;
  private long descent;
  private long leading;
  private long xheight;
  private long overlinePosition;
  private long underlinePosition;
  private long strikethroughPosition;
  private long maxAscent;
  private long maxDescent;
  private long maxHeight;
  private long maxCharAdvance;
  private long italicAngle;
  private boolean uniformFontMetrics;

  public LocalFontMetricsBase() {
    charWidth = new int[65535];
    kerningMap = new HashMap<KerningKey, Integer>();
  }

  public long getAscent() {
    return ascent;
  }

  public void setAscent( final long ascent ) {
    this.ascent = ascent;
  }

  public long getDescent() {
    return descent;
  }

  public void setDescent( final long descent ) {
    this.descent = descent;
  }

  public long getLeading() {
    return leading;
  }

  public void setLeading( final long leading ) {
    this.leading = leading;
  }

  public long getXHeight() {
    return xheight;
  }

  public void setXHeight( final long xheight ) {
    this.xheight = xheight;
  }

  public long getOverlinePosition() {
    return overlinePosition;
  }

  public void setOverlinePosition( final long overlinePosition ) {
    this.overlinePosition = overlinePosition;
  }

  public long getUnderlinePosition() {
    return underlinePosition;
  }

  public void setUnderlinePosition( final long underlinePosition ) {
    this.underlinePosition = underlinePosition;
  }

  public long getStrikeThroughPosition() {
    return strikethroughPosition;
  }

  public void setStrikeThroughPosition( final long strikethroughPosition ) {
    this.strikethroughPosition = strikethroughPosition;
  }

  public long getMaxAscent() {
    return maxAscent;
  }

  public void setMaxAscent( final long maxAscent ) {
    this.maxAscent = maxAscent;
  }

  public long getMaxDescent() {
    return maxDescent;
  }

  public void setMaxDescent( final long maxDescent ) {
    this.maxDescent = maxDescent;
  }

  public long getMaxHeight() {
    return maxHeight;
  }

  public void setMaxHeight( final long maxHeight ) {
    this.maxHeight = maxHeight;
  }

  public long getMaxCharAdvance() {
    return maxCharAdvance;
  }

  public void setMaxCharAdvance( final long maxCharAdvance ) {
    this.maxCharAdvance = maxCharAdvance;
  }

  public long getItalicAngle() {
    return italicAngle;
  }

  public void setItalicAngle( final long italicAngle ) {
    this.italicAngle = italicAngle;
  }

  public boolean isUniformFontMetrics() {
    return uniformFontMetrics;
  }

  public void setUniformFontMetrics( final boolean uniformFontMetrics ) {
    this.uniformFontMetrics = uniformFontMetrics;
  }

  public long getCharWidth( final int codePoint ) {
    if ( codePoint < 0 || codePoint >= charWidth.length ) {
      throw new IndexOutOfBoundsException( "Code-point '" + codePoint + "' is greater than maximum of "
          + charWidth.length );
    }
    return charWidth[codePoint];
  }

  public long getKerning( final int previous, final int codePoint ) {
    final Integer o = kerningMap.get( new KerningKey( previous, codePoint ) );
    if ( o == null ) {
      return 0;
    }
    return o.longValue();
  }

  public BaselineInfo getBaselines( final int codePoint, BaselineInfo info ) {
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
    return info;
  }

  public void setCharWidth( final int codepoint, final int value ) {
    charWidth[codepoint] = value;
  }

  public void setKerning( final int codepoint, final int prev, final int value ) {
    kerningMap.put( new KerningKey( prev, codepoint ), value );
  }
}
