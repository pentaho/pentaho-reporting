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


package org.pentaho.reporting.libraries.fonts.registry;

/**
 * Font-metrics are computed for a certain font-size and style. There are no general metrics, which are assumed to be
 * globally available.
 * <p/>
 * The use of these font metrics is application dependent. As building these metrics usually is memory and CPU
 * intensive, this must be done in an application specific context. You certainly want to cache the font data.
 *
 * @author Thomas Morgner
 */
public interface FontMetrics {
  /**
   * The font ascent is the distance from the font's baseline to the top of most alphanumeric characters. The ascent is
   * always a positive number.
   *
   * @return the ascent.
   */
  public long getAscent();

  /**
   * The font descent is the distance from the font's baseline to the bottom of most alphanumeric characters. The
   * descent is always a positive number.
   *
   * @return the descent.
   */
  public long getDescent();

  /**
   * The standard leading, or interline spacing, is the logical amount of space to be reserved between the descent of
   * one line of text and the ascent of the next line. The height metric is calculated to include this extra space. The
   * leading is always a positive number.
   *
   * @return the leading.
   */
  public long getLeading();

  /**
   * The height of the lowercase 'x'. This is used as hint, which size the lowercase characters will have.
   *
   * @return
   */
  public long getXHeight();

  public long getOverlinePosition();

  public long getUnderlinePosition();

  public long getStrikeThroughPosition();

  public long getMaxAscent();

  public long getMaxDescent();

  public long getMaxHeight();

  public long getMaxCharAdvance();

  public long getCharWidth( int codePoint );

  public long getKerning( int previous, int codePoint );

  public long getItalicAngle();

  /**
   * Baselines are defined for scripts, not glyphs. A glyph carries script information most of the time (unless it is a
   * neutral characters or just weird).
   *
   * @param codePoint
   * @param info
   * @return
   */
  public BaselineInfo getBaselines( int codePoint, BaselineInfo info );

  /**
   * Is it guaranteed that the font always returns the same baseline info objct?
   *
   * @return true, if the baseline info in question is always the same, false otherwise.
   */
  public boolean isUniformFontMetrics();

  public FontNativeContext getNativeContext();

}
