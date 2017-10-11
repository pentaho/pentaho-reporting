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

/**
 * Creation-Date: 23.11.2005, 11:41:24
 *
 * @author Thomas Morgner
 */
public class CSSNumericType extends CSSType {
  public static final CSSNumericType NUMBER = new CSSNumericType( "", false, false );
  public static final CSSNumericType PERCENTAGE = new CSSNumericType( "%", false, false );
  public static final CSSNumericType EM = new CSSNumericType( "em", true, false );
  public static final CSSNumericType EX = new CSSNumericType( "ex", true, false );
  public static final CSSNumericType PX = new CSSNumericType( "px", true, false );

  public static final CSSNumericType CM = new CSSNumericType( "cm", true, true );
  public static final CSSNumericType MM = new CSSNumericType( "mm", true, true );
  public static final CSSNumericType INCH = new CSSNumericType( "inch", true, true );

  public static final CSSNumericType PT = new CSSNumericType( "pt", true, true );
  public static final CSSNumericType PC = new CSSNumericType( "pc", true, true );

  public static final CSSNumericType DEG = new CSSNumericType( "deg", false, false );

  private boolean absolute;
  private boolean length;

  protected CSSNumericType( String name, final boolean length, final boolean absolute ) {
    super( name );
    this.length = length;
    this.absolute = absolute;
  }

  public boolean isLength() {
    return length;
  }

  public boolean isAbsolute() {
    return absolute;
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    return ( obj instanceof CSSNumericType && super.equals( obj ) );
  }

  public int hashCode() {
    return super.hashCode();
  }
}
