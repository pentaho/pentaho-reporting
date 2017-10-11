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
 * Creation-Date: 26.11.2005, 18:31:40
 *
 * @author Thomas Morgner
 */
public class CSSInheritValue implements CSSValue {
  private static CSSInheritValue instance;

  public static synchronized CSSInheritValue getInstance() {
    if ( instance == null ) {
      instance = new CSSInheritValue();
    }
    return instance;
  }

  private CSSInheritValue() {

  }

  public String getCSSText() {
    return "inherit";
  }

  public String toString() {
    return getCSSText();
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}
