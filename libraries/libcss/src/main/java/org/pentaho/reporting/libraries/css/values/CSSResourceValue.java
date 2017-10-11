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
import org.pentaho.reporting.libraries.resourceloader.Resource;

public class CSSResourceValue implements CSSValue {
  private Resource value;
  private CSSValue parent;

  public CSSResourceValue( final Resource value ) {
    this.value = value;
  }

  public CSSResourceValue( final CSSValue parent, final Resource value ) {
    this.parent = parent;
    this.value = value;
  }

  public CSSValue getParent() {
    return parent;
  }

  public Resource getValue() {
    return value;
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }

  public String getCSSText() {
    // this type has no representation in the outside world.
    return null;
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    if ( obj instanceof CSSResourceValue ) {
      CSSResourceValue that = (CSSResourceValue) obj;
      return ( ObjectUtilities.equal( this.parent, that.parent ) && ObjectUtilities.equal( this.value, that.value ) );
    } else {
      return false;
    }
  }
}
