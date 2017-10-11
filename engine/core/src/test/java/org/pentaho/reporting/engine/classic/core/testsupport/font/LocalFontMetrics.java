/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport.font;

import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontNativeContext;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;

public class LocalFontMetrics implements FontMetrics {
  private FontNativeContext record;
  private LocalFontMetricsBase base;
  private float size;

  public LocalFontMetrics( final LocalFontMetricsBase base, final float size ) {
    this.record = new DefaultFontNativeContext( false, false );
    this.base = base;
    this.size = size;
  }

  public long getAscent() {
    return (long) ( size * base.getAscent() / 1000f );
  }

  public BaselineInfo getBaselines( final int codePoint, final BaselineInfo info ) {
    final BaselineInfo baselines = base.getBaselines( codePoint, info );
    if ( baselines == null ) {
      return null;
    }
    final long[] b = baselines.getBaselines();
    for ( int i = 0; i < b.length; i++ ) {
      b[i] = (long) ( size * b[i] / 1000f );
    }
    baselines.setBaselines( b );
    return baselines;
  }

  public long getCharWidth( final int codePoint ) {
    return (long) ( size * base.getCharWidth( codePoint ) / 1000f );
  }

  public long getDescent() {
    return (long) ( size * base.getDescent() / 1000f );
  }

  public long getItalicAngle() {
    return (long) ( size * base.getItalicAngle() / 1000f );
  }

  public long getKerning( final int previous, final int codePoint ) {
    return (long) ( size * base.getKerning( previous, codePoint ) / 1000f );
  }

  public long getLeading() {
    return (long) ( size * base.getLeading() / 1000f );
  }

  public long getMaxAscent() {
    return (long) ( size * base.getMaxAscent() / 1000f );
  }

  public long getMaxCharAdvance() {
    return (long) ( size * base.getMaxCharAdvance() / 1000f );
  }

  public long getMaxDescent() {
    return (long) ( size * base.getMaxDescent() / 1000f );
  }

  public long getMaxHeight() {
    return (long) ( size * base.getMaxHeight() / 1000f );
  }

  public long getOverlinePosition() {
    return (long) ( size * base.getOverlinePosition() / 1000f );
  }

  public long getStrikeThroughPosition() {
    return (long) ( size * base.getStrikeThroughPosition() / 1000f );
  }

  public long getUnderlinePosition() {
    return (long) ( size * base.getUnderlinePosition() / 1000f );
  }

  public long getXHeight() {
    return (long) ( size * base.getXHeight() / 1000f );
  }

  public boolean isUniformFontMetrics() {
    return base.isUniformFontMetrics();
  }

  public FontNativeContext getNativeContext() {
    return record;
  }
}
