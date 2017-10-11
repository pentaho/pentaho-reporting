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
