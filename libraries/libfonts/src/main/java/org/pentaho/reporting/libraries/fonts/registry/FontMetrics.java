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
