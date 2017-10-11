/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.counter.numeric;

public class DevanagariCounterStyle extends NumericCounterStyle {
  public DevanagariCounterStyle() {
    super( 10, "." );
    setReplacementChar( '0', '\u0966' );
    setReplacementChar( '1', '\u0967' );
    setReplacementChar( '2', '\u0968' );
    setReplacementChar( '3', '\u0969' );
    setReplacementChar( '4', '\u096A' );
    setReplacementChar( '5', '\u096b' );
    setReplacementChar( '6', '\u096c' );
    setReplacementChar( '7', '\u096d' );
    setReplacementChar( '8', '\u096e' );
    setReplacementChar( '9', '\u096f' );
  }


}
