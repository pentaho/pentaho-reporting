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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * Creation-Date: 15.04.2007, 14:40:35
 *
 * @author Thomas Morgner
 */
public class LegacyFontMetrics implements FontMetrics {
  private FontNativeContext record;
  private FontMetrics parent;
  private long fontHeight;

  public LegacyFontMetrics( final FontMetrics parent, final double fontHeight ) {
    this.parent = parent;
    this.fontHeight = FontStrictGeomUtility.toInternalValue( fontHeight );
    this.record = parent.getNativeContext();
  }

  /**
   * Is it guaranteed that the font always returns the same baseline info objct?
   *
   * @return true, if the baseline info in question is always the same, false otherwise.
   */
  public boolean isUniformFontMetrics() {
    return parent.isUniformFontMetrics();
  }

  public FontMetrics getParent() {
    return parent;
  }

  public long getAscent() {
    return parent.getAscent();
  }

  public long getDescent() {
    return parent.getDescent();
  }

  public long getLeading() {
    return parent.getLeading();
  }

  public long getXHeight() {
    return parent.getXHeight();
  }

  public long getOverlinePosition() {
    return parent.getOverlinePosition();
  }

  public long getUnderlinePosition() {
    return parent.getUnderlinePosition();
  }

  public long getStrikeThroughPosition() {
    return parent.getStrikeThroughPosition();
  }

  public long getMaxAscent() {
    return parent.getMaxAscent();
  }

  public long getMaxDescent() {
    return parent.getMaxDescent();
  }

  public long getMaxHeight() {
    return fontHeight;
  }

  public long getMaxCharAdvance() {
    return parent.getMaxCharAdvance();
  }

  public long getCharWidth( final int codePoint ) {
    return parent.getCharWidth( codePoint );
  }

  public long getKerning( final int previous, final int codePoint ) {
    return parent.getKerning( previous, codePoint );
  }

  public BaselineInfo getBaselines( final int codePoint, final BaselineInfo info ) {
    return parent.getBaselines( codePoint, info );
  }

  public long getItalicAngle() {
    return parent.getItalicAngle();
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
