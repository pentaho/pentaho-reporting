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
