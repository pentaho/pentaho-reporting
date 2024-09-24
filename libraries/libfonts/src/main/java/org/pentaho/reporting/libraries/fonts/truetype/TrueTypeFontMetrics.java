/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.fonts.truetype;

import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;


/**
 * Creation-Date: 15.12.2005, 12:01:13
 *
 * @author Thomas Morgner
 */
public class TrueTypeFontMetrics implements FontMetrics {
  private ScalableTrueTypeFontMetrics fontMetrics;
  private double fontSize;
  private FontNativeContext record;

  public TrueTypeFontMetrics( final FontNativeContext record,
                              final ScalableTrueTypeFontMetrics fontMetrics,
                              final double fontSize ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException( "The font must not be null" );
    }
    this.record = record;
    this.fontMetrics = fontMetrics;
    this.fontSize = fontSize;
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
    return (long) ( fontSize * fontMetrics.getAscent() );
  }

  public long getDescent() {
    return (long) ( fontSize * fontMetrics.getDescent() );
  }

  public long getLeading() {
    return (long) ( fontSize * fontMetrics.getLeading() );
  }

  public long getXHeight() {
    return (long) ( fontSize * fontMetrics.getXHeight() );
  }

  public long getOverlinePosition() {
    return getLeading() - Math.max( 1000, FontStrictGeomUtility.toInternalValue( fontSize ) / 20 );
  }

  public long getUnderlinePosition() {
    return (long) ( fontSize * fontMetrics.getUnderlinePosition() );
  }

  public long getStrikeThroughPosition() {
    return (long) ( fontSize * fontMetrics.getStrikeThroughPosition() );
  }

  public long getMaxAscent() {
    return (long) ( fontSize * fontMetrics.getMaxAscent() );
  }

  public long getMaxDescent() {
    return (long) ( fontSize * fontMetrics.getMaxDescent() );
  }

  public long getItalicAngle() {
    return fontMetrics.getItalicAngle();
  }

  public long getMaxHeight() {
    return (long) ( ( fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + fontMetrics.getLeading() )
      * fontSize );
  }

  public long getMaxCharAdvance() {
    return (long) ( fontMetrics.getMaxCharAdvance() * fontSize );
  }

  public long getCharWidth( final int character ) {
    return 0;
  }

  public long getKerning( final int previous, final int character ) {
    return 0;
  }

  /**
   * Baselines are defined for scripts, not glyphs. A glyph carries script information most of the time (unless it is a
   * neutral characters or just weird).
   *
   * @param c
   * @return
   */
  public BaselineInfo getBaselines( final int c, final BaselineInfo info ) {
    throw new UnsupportedOperationException( "Not yet implemented." );
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
