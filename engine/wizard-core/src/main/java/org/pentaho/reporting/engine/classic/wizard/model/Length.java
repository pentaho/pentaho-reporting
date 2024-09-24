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
