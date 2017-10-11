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
 * Creation-Date: 23.11.2005, 11:50:28
 *
 * @author Thomas Morgner
 */
public class CSSStringValue implements CSSValue {
  private CSSStringType type;
  private String value;

  public CSSStringValue( final CSSStringType type, final String value ) {
    this.type = type;
    this.value = value;
  }

  public CSSType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    return getCSSText();
  }

  public boolean equals( final Object obj ) {
    if ( obj instanceof CSSStringValue ) {
      CSSStringValue that = (CSSStringValue) obj;
      return ( ObjectUtilities.equal( this.type, that.type ) && ObjectUtilities.equal( this.value, that.value ) );
    }
    return false;
  }

  public String getCSSText() {
    if ( type == CSSStringType.URI ) {
      return "uri(" + value + ")";
    } else {
      return "\"" + value + "\"";
    }
  }
}
