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
 * Creation-Date: 28.11.2005, 16:36:30
 *
 * @author Thomas Morgner
 */
public class FontFamilyValues {
  public static final CSSConstant SERIF = new CSSConstant( "serif" );
  public static final CSSConstant SANS_SERIF = new CSSConstant( "sans-serif" );
  public static final CSSConstant FANTASY = new CSSConstant( "fantasy" );
  public static final CSSConstant CURSIVE = new CSSConstant( "cursive" );
  public static final CSSConstant MONOSPACE = new CSSConstant( "monospace" );
  public static final CSSConstant NONE = new CSSConstant( "none" );

  private FontFamilyValues() {
  }
}
