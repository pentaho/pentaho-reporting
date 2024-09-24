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

package org.pentaho.reporting.libraries.css.keys.column;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 03.12.2005, 21:11:43
 *
 * @author Thomas Morgner
 */
public class ColumnSpaceDistribution {
  // [start || end || inner || outer || between] | inherit
  public static final CSSConstant START =
    new CSSConstant( "start" );
  public static final CSSConstant END =
    new CSSConstant( "end" );
  public static final CSSConstant INNER =
    new CSSConstant( "inner" );
  public static final CSSConstant OUTER =
    new CSSConstant( "outer" );
  public static final CSSConstant BETWEEN =
    new CSSConstant( "between" );

  private ColumnSpaceDistribution() {
  }
}
