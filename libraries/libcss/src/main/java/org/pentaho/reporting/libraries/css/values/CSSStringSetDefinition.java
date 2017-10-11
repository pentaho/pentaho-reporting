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
 * Creation-Date: 01.12.2005, 18:21:53
 *
 * @author Thomas Morgner
 */
public class CSSStringSetDefinition implements CSSValue {
  private String identifier;
  private CSSValue value;

  public CSSStringSetDefinition( final String identifier, final CSSValue value ) {
    this.identifier = identifier;
    this.value = value;
  }

  public String getIdentifier() {
    return identifier;
  }

  public CSSValue getValue() {
    return value;
  }

  public String getCSSText() {
    return identifier + " " + value;
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
    if ( obj instanceof CSSStringSetDefinition ) {
      CSSStringSetDefinition that = (CSSStringSetDefinition) obj;
      return ( ObjectUtilities.equal( this.identifier, that.identifier ) && ObjectUtilities
        .equal( this.value, that.value ) );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
