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

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 03.12.2005, 20:48:04 * column-count column-width column-min-width column-width-policy
 * <p/>
 * The second group of properties describes the space between columns:
 * <p/>
 * column-gap column-rule column-rule-color column-rule-style column-rule-width
 * <p/>
 * The third group consists of one property which make it possible an element to span several columns:
 * <p/>
 * column-span
 *
 * @author Thomas Morgner
 */
public class ColumnStyleKeys {
  public static final StyleKey COLUMN_COUNT =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-count", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_SPACE_DISTRIBUTION =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-space-distribution", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-width", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_MIN_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-min-width", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_WIDTH_POLICY =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-width-policy", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_GAP =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-gap", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_RULE_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-rule-color", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_RULE_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-rule-style", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_RULE_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-rule-width", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey COLUMN_SPAN =
    StyleKeyRegistry.getRegistry().createKey
      ( "column-span", false, false, StyleKey.BLOCK_ELEMENTS );

  /**
   * Defines, whether column contents should be balanced.
   * <p/>
   * Another idea stolen from OpenOffice :)
   */
  public static final StyleKey COLUMN_BALANCE =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-column-balance", false, false, StyleKey.BLOCK_ELEMENTS );


  private ColumnStyleKeys() {
  }
}
