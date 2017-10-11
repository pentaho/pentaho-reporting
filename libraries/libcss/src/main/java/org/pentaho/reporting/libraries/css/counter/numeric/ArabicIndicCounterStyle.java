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

public class ArabicIndicCounterStyle extends NumericCounterStyle {
  public ArabicIndicCounterStyle() {
    super( 10, "." );
    setReplacementChar( '0', '\u0660' );
    setReplacementChar( '1', '\u0661' );
    setReplacementChar( '2', '\u0662' );
    setReplacementChar( '3', '\u0663' );
    setReplacementChar( '4', '\u0664' );
    setReplacementChar( '5', '\u0665' );
    setReplacementChar( '6', '\u0666' );
    setReplacementChar( '7', '\u0667' );
    setReplacementChar( '8', '\u0668' );
    setReplacementChar( '9', '\u0669' );
  }


}
