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
 * Creation-Date: 27.11.2005, 20:18:52
 *
 * @author Thomas Morgner
 */
public class CSSFunctionValue implements CSSValue {
  private String functionName;
  private CSSValue[] parameters;

  public CSSFunctionValue( final String functionName,
                           final CSSValue[] parameters ) {
    if ( functionName == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }
    this.functionName = functionName;
    this.parameters = (CSSValue[]) parameters.clone();
  }

  public String getFunctionName() {
    return functionName;
  }

  public CSSValue[] getParameters() {
    return (CSSValue[]) parameters.clone();
  }

  public String getCSSText() {
    StringBuffer b = new StringBuffer();
    b.append( functionName );
    b.append( '(' );
    for ( int i = 0; i < parameters.length; i++ ) {
      if ( i != 0 ) {
        b.append( ',' );
      }
      CSSValue parameter = parameters[ i ];
      b.append( parameter.getCSSText() );
    }
    b.append( ')' );
    return b.toString();
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return getCSSText();
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSFunctionValue ) {
      CSSFunctionValue that = (CSSFunctionValue) obj;
      return ( ObjectUtilities.equal( this.functionName, that.functionName )
        && ObjectUtilities.equalArray( this.parameters, that.parameters ) );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
