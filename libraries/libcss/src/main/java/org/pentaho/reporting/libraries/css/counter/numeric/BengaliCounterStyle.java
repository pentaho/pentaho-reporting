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

public class BengaliCounterStyle extends NumericCounterStyle {
  public BengaliCounterStyle() {
    super( 10, "." );
    setReplacementChar( '0', '\u09e6' );
    setReplacementChar( '1', '\u09e7' );
    setReplacementChar( '2', '\u09e8' );
    setReplacementChar( '3', '\u09e9' );
    setReplacementChar( '4', '\u09ea' );
    setReplacementChar( '5', '\u09eb' );
    setReplacementChar( '6', '\u09ec' );
    setReplacementChar( '7', '\u09ed' );
    setReplacementChar( '8', '\u09ee' );
    setReplacementChar( '9', '\u09ef' );
  }


}
