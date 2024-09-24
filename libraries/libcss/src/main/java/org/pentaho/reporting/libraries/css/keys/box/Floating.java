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

package org.pentaho.reporting.libraries.css.keys.box;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Defines the floating property. Floating elements create a new flow inside an existing flow.
 * <p/>
 * The properties left and top are equivalent, as as right and bottom. All properties in the specification can be
 * reduced to either left or right in the computation phase.
 * <p/>
 * Floating images cannot leave their containing block vertically or horizontally. If negative margins are given, they
 * may be shifted outside the content area, but vertical margins will increase the 'empty-space' between the blocks
 * instead of messing up the previous element.
 *
 * @author Thomas Morgner
 */
public class Floating {
  public static final CSSConstant LEFT = new CSSConstant( "left" );
  public static final CSSConstant RIGHT = new CSSConstant( "right" );
  public static final CSSConstant TOP = new CSSConstant( "top" );
  public static final CSSConstant BOTTOM = new CSSConstant( "bottom" );
  public static final CSSConstant INSIDE = new CSSConstant( "inside" );
  public static final CSSConstant OUTSIDE = new CSSConstant( "outside" );
  public static final CSSConstant START = new CSSConstant( "start" );
  public static final CSSConstant END = new CSSConstant( "end" );
  public static final CSSConstant NONE = new CSSConstant( "none" );

  // from the column stuff
  public static final CSSConstant IN_COLUMN = new CSSConstant( "in-column" );
  public static final CSSConstant MID_COLUMN = new CSSConstant( "mid-column" );

  private Floating() {
  }

}
