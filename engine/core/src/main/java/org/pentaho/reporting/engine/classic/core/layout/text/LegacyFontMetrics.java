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
