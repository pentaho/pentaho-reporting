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

package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 16:38:22
 *
 * @author Thomas Morgner
 */
public class LineStackingStrategy {
  public static final CSSConstant INLINE_LINE_HEIGHT =
    new CSSConstant( "inline-line-height" );
  public static final CSSConstant BLOCK_LINE_HEIGHT =
    new CSSConstant( "block-line-height" );
  public static final CSSConstant MAX_LINE_HEIGHT =
    new CSSConstant( "max-line-height" );
  public static final CSSConstant GRID_HEIGHT =
    new CSSConstant( "grid-height" );


  private LineStackingStrategy() {
  }
}
