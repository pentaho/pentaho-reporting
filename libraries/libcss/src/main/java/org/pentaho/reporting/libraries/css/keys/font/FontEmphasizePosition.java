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

package org.pentaho.reporting.libraries.css.keys.font;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Defines the emphasize marker position in asian texts. If the text layout is horizontal (ie Roman style), before means
 * above the text, and after means below the text.
 * <p/>
 * See: CSS3-Fonts ?4-3
 *
 * @author Thomas Morgner
 */
public class FontEmphasizePosition {
  public static final CSSConstant BEFORE =
    new CSSConstant( "before" );
  public static final CSSConstant AFTER =
    new CSSConstant( "after" );
  public static final CSSConstant ABOVE =
    new CSSConstant( "above" );
  public static final CSSConstant BELOW =
    new CSSConstant( "below" );

  private FontEmphasizePosition() {
  }
}
