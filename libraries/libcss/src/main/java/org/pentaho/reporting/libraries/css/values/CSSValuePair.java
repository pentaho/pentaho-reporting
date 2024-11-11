/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.values;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 15.04.2006, 10:56:53
 *
 * @author Thomas Morgner
 */
public class CSSValuePair implements CSSValue {
  private CSSValue firstValue;
  private CSSValue secondValue;

  public CSSValuePair( final CSSValue firstValue ) {
    if ( firstValue == null ) {
      throw new NullPointerException();
    }
    this.firstValue = firstValue;
    this.secondValue = firstValue;
  }

  public CSSValuePair( final CSSValue firstValue, final CSSValue secondValue ) {
    if ( firstValue == null ) {
      throw new NullPointerException();
    }
    if ( secondValue == null ) {
      throw new NullPointerException();
    }
    this.firstValue = firstValue;
    this.secondValue = secondValue;
  }

  public CSSValue getFirstValue() {
    return firstValue;
  }

  public CSSValue getSecondValue() {
    return secondValue;
  }

  public String getCSSText() {
    return firstValue.getCSSText() + " " + secondValue.getCSSText();
  }

  public String toString() {
    return getCSSText();
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSValuePair ) {
      CSSValuePair that = (CSSValuePair) obj;
      return ( ObjectUtilities.equal( this.firstValue, that.firstValue ) && ObjectUtilities
        .equal( this.secondValue, that.secondValue ) );
    } else {
      return false;
    }
  }


  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
