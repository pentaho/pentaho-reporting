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

package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 19:37:35
 *
 * @author Thomas Morgner
 */
public class BorderStyle {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant HIDDEN = new CSSConstant( "hidden" );
  public static final CSSConstant DOTTED = new CSSConstant( "dotted" );
  public static final CSSConstant DASHED = new CSSConstant( "dashed" );
  public static final CSSConstant SOLID = new CSSConstant( "solid" );
  public static final CSSConstant DOUBLE = new CSSConstant( "double" );
  public static final CSSConstant DOT_DASH = new CSSConstant( "dot-dash" );
  public static final CSSConstant DOT_DOT_DASH = new CSSConstant( "dot-dot-dash" );
  public static final CSSConstant WAVE = new CSSConstant( "wave" );
  public static final CSSConstant GROOVE = new CSSConstant( "groove" );
  public static final CSSConstant RIDGE = new CSSConstant( "ridge" );
  public static final CSSConstant INSET = new CSSConstant( "inset" );
  public static final CSSConstant OUTSET = new CSSConstant( "outset" );

  private BorderStyle() {
  }
}
