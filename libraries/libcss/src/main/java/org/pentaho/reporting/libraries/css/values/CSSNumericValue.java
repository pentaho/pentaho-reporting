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

package org.pentaho.reporting.libraries.css.values;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 23.11.2005, 11:37:44
 *
 * @author Thomas Morgner
 */
public class CSSNumericValue implements CSSValue {
  /**
   *
   */
  private static final long serialVersionUID = 3906005900395358108L;

  public static final CSSNumericValue ZERO_LENGTH = CSSNumericValue.createValue( CSSNumericType.PT, 0 );

  private double value;
  private CSSNumericType type;

  protected CSSNumericValue( final CSSNumericType type, final double value ) {
    if ( type == null ) {
      throw new NullPointerException();
    }
    this.type = type;
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  public CSSType getType() {
    return type;
  }

  public CSSNumericType getNumericType() {
    return type;
  }

  public String getCSSText() {
    final String typeText = type.getType();
    final double value = getValue();
    if ( typeText.length() == 0 ) {
      if ( Math.floor( value ) == value ) {
        return String.valueOf( (long) value );
      }
      return String.valueOf( value );
    }

    if ( Math.floor( value ) == value ) {
      return String.valueOf( (long) value ) + typeText; //$NON-NLS-1$
    }
    return value + typeText; //$NON-NLS-1$
  }

  public String toString() {
    return getCSSText();
  }

  public static CSSNumericValue createPtValue( final double value ) {
    return new CSSNumericValue( CSSNumericType.PT, value );
  }

  public static CSSNumericValue createValue( final CSSNumericType type,
                                             final double value ) {
    return new CSSNumericValue( type, value );
  }

  /**
   * Compares the input obj parameter to the current object and returns true if equal.
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSNumericValue == false ) {
      return false;
    }

    final CSSNumericValue that = (CSSNumericValue) obj;
    if ( this.value != that.value ) {
      return false;
    }
    if ( !ObjectUtilities.equal( this.type, that.type ) ) {
      return false;
    }
    return true;
  }
} 
