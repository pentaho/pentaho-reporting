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

package org.pentaho.reporting.libraries.css.keys.list;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 01.12.2005, 18:40:19
 *
 * @author Thomas Morgner
 */
public class ListStyleTypeGlyphs {
  // box | check | circle | diamond | disc | hyphen | square
  public static final CSSConstant BOX = new CSSConstant( "box" );
  public static final CSSConstant CHECK = new CSSConstant( "check" );
  public static final CSSConstant CIRCLE = new CSSConstant( "circle" );
  public static final CSSConstant DIAMOND = new CSSConstant( "diamon" );
  public static final CSSConstant DISC = new CSSConstant( "disc" );
  public static final CSSConstant HYPHEN = new CSSConstant( "hyphen" );
  public static final CSSConstant SQUARE = new CSSConstant( "square" );

  private ListStyleTypeGlyphs() {
  }
}
