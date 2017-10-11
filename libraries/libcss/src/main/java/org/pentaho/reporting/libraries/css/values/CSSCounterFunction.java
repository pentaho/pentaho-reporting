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
 * Creation-Date: 23.11.2005, 12:04:56
 *
 * @author Thomas Morgner
 */
public class CSSCounterFunction implements CSSValue {
  private String identifier;
  private String listStyle;
  private String separator;

  public CSSCounterFunction( final String identifier,
                             final String listStyle,
                             final String separator ) {
    this.identifier = identifier;
    this.listStyle = listStyle;
    this.separator = separator;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getListStyle() {
    return listStyle;
  }

  public String getSeparator() {
    return separator;
  }

  public String getCSSText() {
    return "counter(" + identifier + ", \"" + separator + "\", " + listStyle + ")";
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
    if ( obj instanceof CSSCounterFunction ) {
      CSSCounterFunction that = (CSSCounterFunction) obj;
      return ( ObjectUtilities.equal( this.identifier, that.identifier )
        && ObjectUtilities.equal( this.listStyle, that.listStyle )
        && ObjectUtilities.equal( this.separator, that.separator ) );
    } else {
      return false;
    }
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
