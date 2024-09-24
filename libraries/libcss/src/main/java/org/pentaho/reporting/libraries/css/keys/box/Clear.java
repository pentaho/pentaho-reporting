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
 * Creation-Date: 28.11.2005, 15:55:25
 *
 * @author Thomas Morgner
 */
public class Clear {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant LEFT = new CSSConstant( "left" );
  public static final CSSConstant RIGHT = new CSSConstant( "right" );
  public static final CSSConstant TOP = new CSSConstant( "top" );
  public static final CSSConstant BOTTOM = new CSSConstant( "bottom" );
  public static final CSSConstant INSIDE = new CSSConstant( "inside" );
  public static final CSSConstant OUTSIDE = new CSSConstant( "outside" );
  public static final CSSConstant START = new CSSConstant( "start" );
  public static final CSSConstant END = new CSSConstant( "end" );
  public static final CSSConstant BOTH = new CSSConstant( "both" );

  private Clear() {
  }
}
