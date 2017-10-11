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

package org.pentaho.reporting.libraries.css.keys.color;

import org.pentaho.reporting.libraries.css.values.CSSColorValue;

/**
 * Contains all colors defined for HTML 4.01
 *
 * @author Thomas Morgner
 */
public final class HtmlColors {
  public static final CSSColorValue BLACK = new CSSColorValue( 0x000000, false );
  public static final CSSColorValue GREEN = new CSSColorValue( 0x008000, false );
  public static final CSSColorValue SILVER = new CSSColorValue( 0xC0C0C0, false );
  public static final CSSColorValue LIME = new CSSColorValue( 0x00FF00, false );
  public static final CSSColorValue GRAY = new CSSColorValue( 0x808080, false );
  public static final CSSColorValue OLIVE = new CSSColorValue( 0x808000, false );
  public static final CSSColorValue WHITE = new CSSColorValue( 0xFFFFFF, false );
  public static final CSSColorValue YELLOW = new CSSColorValue( 0xFFFF00, false );
  public static final CSSColorValue MAROON = new CSSColorValue( 0x800000, false );
  public static final CSSColorValue NAVY = new CSSColorValue( 0x000080, false );
  public static final CSSColorValue RED = new CSSColorValue( 0xFF0000, false );
  public static final CSSColorValue BLUE = new CSSColorValue( 0x0000FF, false );
  public static final CSSColorValue PURPLE = new CSSColorValue( 0x800080, false );
  public static final CSSColorValue TEAL = new CSSColorValue( 0x008080, false );
  public static final CSSColorValue FUCHSIA = new CSSColorValue( 0xFF00FF, false );
  public static final CSSColorValue AQUA = new CSSColorValue( 0x00FFFF, false );

  private HtmlColors() {
  }
}
