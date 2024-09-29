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

/**
 * Creation-Date: 25.11.2005, 18:22:54
 *
 * @author Thomas Morgner
 */
public final class CSSConstant implements CSSValue {
  private String constant;

  public CSSConstant( final String constant ) {
    if ( constant == null ) {
      throw new NullPointerException( "Constant must not be null" );
    }
    this.constant = constant.toLowerCase();
  }

  public String getCSSText() {
    return constant;
  }

  public final boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    // we excplicitly check all subclasses as well. A constant is always defined
    // by its string value.
    if ( o instanceof CSSConstant == false ) {
      return false;
    }

    final CSSConstant that = (CSSConstant) o;

    if ( !constant.equals( that.constant ) ) {
      return false;
    }

    return true;
  }

  public final int hashCode() {
    return constant.hashCode();
  }

  public String toString() {
    return getCSSText();
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
