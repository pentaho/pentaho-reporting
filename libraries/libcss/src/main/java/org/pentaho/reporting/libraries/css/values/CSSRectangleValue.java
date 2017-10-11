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
 * Creation-Date: 23.11.2005, 12:04:06
 *
 * @author Thomas Morgner
 */
public class CSSRectangleValue implements CSSValue {
  private CSSRectangleType type;
  private CSSNumericValue top;
  private CSSNumericValue left;
  private CSSNumericValue bottom;
  private CSSNumericValue right;

  public CSSRectangleValue( final CSSRectangleType type,
                            final CSSNumericValue top,
                            final CSSNumericValue right,
                            final CSSNumericValue bottom,
                            final CSSNumericValue left ) {
    this.type = type;
    this.top = top;
    this.left = left;
    this.bottom = bottom;
    this.right = right;
  }

  public CSSNumericValue getTop() {
    return top;
  }

  public CSSNumericValue getLeft() {
    return left;
  }

  public CSSNumericValue getBottom() {
    return bottom;
  }

  public CSSNumericValue getRight() {
    return right;
  }

  public String getCSSText() {
    return toString();
  }

  public CSSType getType() {
    return type;
  }

  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    buffer.append( type.getType() );
    buffer.append( "(" );
    buffer.append( top );
    buffer.append( ", " );
    buffer.append( left );
    buffer.append( ", " );
    buffer.append( bottom );
    buffer.append( ", " );
    buffer.append( right );
    buffer.append( ")" );
    return buffer.toString();
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSRectangleValue ) {
      CSSRectangleValue that = (CSSRectangleValue) obj;
      return ( ObjectUtilities.equal( this.bottom, that.bottom )
        && ObjectUtilities.equal( this.left, that.left )
        && ObjectUtilities.equal( this.right, that.right )
        && ObjectUtilities.equal( this.top, that.top )
        && ObjectUtilities.equal( this.type, that.type ) );
    } else {
      return false;
    }
  }

}
