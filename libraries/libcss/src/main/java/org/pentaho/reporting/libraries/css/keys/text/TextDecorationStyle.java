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

package org.pentaho.reporting.libraries.css.keys.text;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Used for underline, strike-through and overline.
 *
 * @author Thomas Morgner
 */
public class TextDecorationStyle {
  // none | solid | double | dotted | dashed | dot-dash | dot-dot-dash | wave
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant SOLID = new CSSConstant( "solid" );
  public static final CSSConstant DOUBLE = new CSSConstant( "double" );
  public static final CSSConstant DOTTED = new CSSConstant( "dotted" );
  public static final CSSConstant DASHED = new CSSConstant( "dashed" );
  public static final CSSConstant DOT_DASH = new CSSConstant( "dot-dash" );
  public static final CSSConstant DOT_DOT_DASH = new CSSConstant( "dot-dot-dash" );
  public static final CSSConstant WAVE = new CSSConstant( "wave" );
  // This is an open-office addition ...
  public static final CSSConstant LONG_DASH = new CSSConstant( "-x-pentaho-css-long-dash" );

  private TextDecorationStyle() {
  }
}
