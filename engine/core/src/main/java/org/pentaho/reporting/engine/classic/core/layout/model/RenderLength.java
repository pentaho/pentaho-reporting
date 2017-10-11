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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Creation-Date: 09.07.2006, 21:03:12
 *
 * @author Thomas Morgner
 */
public final class RenderLength {
  public static final RenderLength AUTO = new RenderLength( Long.MIN_VALUE, false );
  public static final RenderLength EMPTY = new RenderLength( 0, false );

  private long value;
  private boolean percentage;

  public RenderLength( final long value, final boolean percentage ) {
    this.value = value;
    this.percentage = percentage;
  }

  public long getValue() {
    return value;
  }

  public boolean isPercentage() {
    return percentage;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final RenderLength that = (RenderLength) o;

    if ( percentage != that.percentage ) {
      return false;
    }
    if ( value != that.value ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = (int) ( value ^ ( value >>> 32 ) );
    result = 29 * result + ( percentage ? 1 : 0 );
    return result;
  }

  public strictfp long resolve( final long parent ) {
    if ( isPercentage() ) {
      return StrictMath.round( StrictGeomUtility.multiply( value, parent ) / 100.0 );
    } else if ( value == Long.MIN_VALUE ) {
      return 0;
    } else {
      return value;
    }
  }

  public strictfp long resolve( final long parent, final long auto ) {
    if ( isPercentage() ) {
      return StrictMath.round( StrictGeomUtility.multiply( value, parent ) / 100.0 );
    } else if ( value == Long.MIN_VALUE ) {
      return auto;
    } else {
      return value;
    }
  }

  public static strictfp long resolveLength( final long parent, final double rawvalue ) {
    final long value = StrictGeomUtility.toInternalValue( rawvalue );
    if ( value == Long.MIN_VALUE ) {
      return 0;
    }
    if ( value < 0 ) {
      return -( StrictMath.round( StrictGeomUtility.multiply( value, parent ) / 100.0 ) );
    } else {
      return value;
    }
  }

  public static RenderLength createFromRaw( final double rawValue ) {
    if ( rawValue <= Long.MIN_VALUE ) {
      return RenderLength.AUTO;
    }
    if ( rawValue < 0 ) {
      return new RenderLength( StrictGeomUtility.toInternalValue( -rawValue ), true );
    }
    if ( rawValue == 0 ) {
      return EMPTY;
    }
    return new RenderLength( StrictGeomUtility.toInternalValue( rawValue ), false );
  }

  public static RenderLength createPercentage( final double rawValue ) {
    if ( rawValue <= Long.MIN_VALUE || rawValue >= Long.MAX_VALUE ) {
      throw new IllegalArgumentException();
    }
    if ( rawValue < 0 ) {
      throw new IllegalArgumentException();
    }
    if ( rawValue == 0 ) {
      return RenderLength.EMPTY;
    }

    return new RenderLength( StrictGeomUtility.toInternalValue( rawValue ), true );
  }

  public strictfp RenderLength resolveToRenderLength( final long parent ) {
    if ( isPercentage() ) {
      if ( parent <= 0 ) {
        // An unresolvable parent ...
        return RenderLength.AUTO;
      }
      // This may resolve to zero - which is valid
      final long value = (int) ( StrictMath.round( StrictGeomUtility.multiply( this.value, parent ) / 100.0 ) );
      return new RenderLength( value, false );
    } else if ( value <= 0 ) {
      return RenderLength.AUTO;
    } else {
      return new RenderLength( value, false );
    }
  }

  public String toString() {
    if ( value == Long.MIN_VALUE ) {
      return "RenderLength{value=AUTO}";
    }
    if ( isPercentage() ) {
      return "RenderLength{" + "value=" + StrictGeomUtility.toExternalValue( value ) + "% }";
    } else {
      return "RenderLength{" + "value=" + value + "micro-pt }";
    }
  }
}
