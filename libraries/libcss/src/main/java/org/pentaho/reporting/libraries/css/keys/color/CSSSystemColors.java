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
import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * A set of color mappings which map the constants to system default values. This *could* also include colors which map
 * to standard UI colors, like 'caption background' etc.
 *
 * @author Thomas Morgner
 */
public final class CSSSystemColors {
  private CSSSystemColors() {
  }

  public static final CSSColorValue TRANSPARENT = new CSSColorValue( 0, 0, 0, 0 );
  public static final CSSConstant CURRENT_COLOR = new CSSConstant( "currentColor" );
}
