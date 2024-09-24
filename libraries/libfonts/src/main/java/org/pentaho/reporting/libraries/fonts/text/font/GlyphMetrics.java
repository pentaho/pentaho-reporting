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

package org.pentaho.reporting.libraries.fonts.text.font;

/**
 * A return value for glyph metrics. It is used as return value by the font size producer.
 *
 * @author Thomas Morgner
 */
public class GlyphMetrics {
  private int width;
  private int height;
  private int baselinePosition;

  public GlyphMetrics() {
  }

  public int getWidth() {
    return width;
  }

  public void setWidth( final int width ) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight( final int height ) {
    this.height = height;
  }

  public int getBaselinePosition() {
    return baselinePosition;
  }

  public void setBaselinePosition( final int baselinePosition ) {
    this.baselinePosition = baselinePosition;
  }
}
