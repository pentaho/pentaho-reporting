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

public class TextDecorationWidth {
  /**
   * A bold is basicly an auto, that is thicker than the normal auto value.
   */
  public static final CSSConstant BOLD = new CSSConstant( "bold" );

  /**
   * A dash is basicly an auto, that is thinner than the normal auto value.
   */
  public static final CSSConstant DASH = new CSSConstant( "dash" );

  /**
   * The text decoration width is the normal text decoration width for the nominal font. If no font characteristic
   * exists for the width of the text decoration in question, the user agent should proceed as though 'auto' were
   * specified.
   * <p/>
   * The computed value is 'normal'.
   */
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );

  private TextDecorationWidth() {
  }
}
