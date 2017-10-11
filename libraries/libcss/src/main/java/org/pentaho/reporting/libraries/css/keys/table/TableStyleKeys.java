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

package org.pentaho.reporting.libraries.css.keys.table;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 23.06.2006, 15:20:03
 *
 * @author Thomas Morgner
 */
public class TableStyleKeys {
  /**
   * Enumeration: Show, hide, inherit
   */
  public static final StyleKey EMPTY_CELLS =
    StyleKeyRegistry.getRegistry().createKey
      ( "empty-cells", false, true, StyleKey.All_ELEMENTS );

  /**
   * Pair of length; No percentages; Inheritable
   */
  public static final StyleKey BORDER_SPACING =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-spacing", false, true, StyleKey.All_ELEMENTS );

  /**
   * Pair of length; No percentages; Inheritable
   */
  public static final StyleKey BORDER_COLLAPSE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-collapse", false, true, StyleKey.All_ELEMENTS );

  /**
   * Auto or fixed.
   */
  public static final StyleKey TABLE_LAYOUT =
    StyleKeyRegistry.getRegistry().createKey
      ( "table-layout", false, true, StyleKey.All_ELEMENTS );

  /**
   * top or bottom.
   */
  public static final StyleKey CAPTION_SIDE =
    StyleKeyRegistry.getRegistry().createKey
      ( "caption-side", false, true, StyleKey.All_ELEMENTS );

  public static final StyleKey ROW_SPAN =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-rowspan", false, false, StyleKey.All_ELEMENTS );
  public static final StyleKey COL_SPAN =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-colspan", false, false, StyleKey.All_ELEMENTS );


}
