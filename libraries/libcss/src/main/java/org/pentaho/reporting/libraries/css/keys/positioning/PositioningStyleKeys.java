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

package org.pentaho.reporting.libraries.css.keys.positioning;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 08.12.2005, 14:50:40
 *
 * @author Thomas Morgner
 */
public class PositioningStyleKeys {
  /**
   * Width and height are defined in the Box-module.
   */

  public static final StyleKey TOP =
    StyleKeyRegistry.getRegistry().createKey
      ( "top", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey LEFT =
    StyleKeyRegistry.getRegistry().createKey
      ( "left", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BOTTOM =
    StyleKeyRegistry.getRegistry().createKey
      ( "bottom", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey RIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "right", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "position", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey Z_INDEX =
    StyleKeyRegistry.getRegistry().createKey
      ( "z-index", false, false, StyleKey.All_ELEMENTS );


  private PositioningStyleKeys() {
  }
}
