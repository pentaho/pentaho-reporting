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

package org.pentaho.reporting.engine.classic.wizard.model;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

public final class Length implements Serializable {
  private double value;
  private LengthUnit unit;

  public Length( final LengthUnit unit, final double value ) {
    if ( value < 0 ) {
      throw new IllegalArgumentException();
    }
    if ( unit == null ) {
      throw new NullPointerException();
    }
    this.unit = unit;
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  public Float getNormalizedValue() {
    return new Float( unit.convertToPoints( value ) );
  }

  public LengthUnit getUnit() {
    return unit;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof Length ) ) {
      return false;
    }

    final Length length = (Length) o;

    if ( Double.compare( length.value, value ) != 0 ) {
      return false;
    }
    if ( !unit.equals( length.unit ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    final long temp = value != +0.0d ? Double.doubleToLongBits( value ) : 0L;
    int result = (int) ( temp ^ ( temp >>> 32 ) );
    result = 31 * result + unit.hashCode();
    return result;
  }

  public static Length parseLength( final String s ) throws IllegalArgumentException {
    if ( s == null ) {
      return null;
    }
    try {
      final LengthUnit[] lengthUnits = LengthUnit.values();
      for ( int i = 0; i < lengthUnits.length; i++ ) {
        final LengthUnit lengthUnit = lengthUnits[ i ];
        final String name = lengthUnit.getName();
        if ( StringUtils.endsWithIgnoreCase( s, name ) ) {
          final String number = s.substring( 0, s.length() - name.length() ).trim();
          final double v = Double.parseDouble( number.trim() );
          return new Length( lengthUnit, v );
        }
      }

      final double v = Double.parseDouble( s.trim() );
      return new Length( LengthUnit.POINTS, v );
    } catch ( final NumberFormatException nfe ) {
      throw new IllegalArgumentException( "Length '" + s + "' cannot be parsed." );
    }
  }

  public String toString() {
    final FastDecimalFormat numberInstance = new FastDecimalFormat( "#0.####", Locale.US );
    return numberInstance.format( new BigDecimal( value ) ) + unit.getName();
  }
}
