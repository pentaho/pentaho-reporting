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
 * Creation-Date: 01.02.2006, 22:10:01
 *
 * @author Thomas Morgner
 */
public class DefaultFontContext implements FontContext {
  private double fontSize;
  private boolean antiAliased;
  private boolean fractionalMetrics;
  private boolean embedded;
  private String encoding;

  public DefaultFontContext( final double fontSize,
                             final boolean antiAliased,
                             final boolean fractionalMetrics,
                             final boolean embedded,
                             final String encoding ) {
    this.embedded = embedded;
    this.encoding = encoding;
    this.fontSize = fontSize;
    this.antiAliased = antiAliased;
    this.fractionalMetrics = fractionalMetrics;
  }

  /**
   * This is controlled by the output target and the stylesheet. If the output target does not support aliasing, it
   * makes no sense to enable it and all such requests are ignored.
   *
   * @return
   */
  public boolean isAntiAliased() {
    return antiAliased;
  }

  /**
   * This is defined by the output target. This is not controlled by the stylesheet.
   *
   * @return
   */
  public boolean isFractionalMetrics() {
    return fractionalMetrics;
  }

  /**
   * The requested font size. A font may have a fractional font size (ie. 8.5 point). The font size may be influenced by
   * the output target.
   *
   * @return the font size.
   */
  public double getFontSize() {
    return fontSize;
  }

  public boolean isEmbedded() {
    return embedded;
  }

  public String getEncoding() {
    return encoding;
  }
}
