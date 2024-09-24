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

package org.pentaho.reporting.libraries.css.keys.page;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 30.11.2005, 17:11:54
 *
 * @author Thomas Morgner
 */
public class PageStyleKeys {
  /**
   * The physical Page Size
   */
  public static final StyleKey SIZE =
    StyleKeyRegistry.getRegistry().createKey
      ( "size", false, false, StyleKey.PAGE_CONTEXT );

  //// This stuff is output specific and therefore must be declared in the processor itself
  //// Such style-keys will be implemented in the Classic-Engine itself
  //
  //  /**
  //   * The logical Page Size
  //   */
  //  public static final StyleKey LOGICAL_SIZE =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-logical-size", false, false, StyleKey.PAGE_CONTEXT);
  //
  //  /**
  //   * A dimension ({length}{2}) that defines how often the page is repeated
  //   * horizontally and vertically if the content does not fit.
  //   */
  //  public static final StyleKey HORIZONTAL_PAGE_SPAN =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-horizontal-page-span", false, false, StyleKey.PAGE_CONTEXT);
  //
  //  public static final StyleKey VERTICAL_PAGE_SPAN =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-vertical-page-span", false, false, StyleKey.PAGE_CONTEXT);

  public static final StyleKey PAGE_BREAK_BEFORE =
    StyleKeyRegistry.getRegistry().createKey
      ( "page-break-before", false, true, StyleKey.BLOCK_ELEMENTS );
  public static final StyleKey PAGE_BREAK_AFTER =
    StyleKeyRegistry.getRegistry().createKey
      ( "page-break-after", false, true, StyleKey.BLOCK_ELEMENTS );
  public static final StyleKey PAGE_BREAK_INSIDE =
    StyleKeyRegistry.getRegistry().createKey
      ( "page-break-inside", false, true, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey PAGE =
    StyleKeyRegistry.getRegistry().createKey
      ( "page", false, true, StyleKey.BLOCK_ELEMENTS );
  public static final StyleKey PAGE_POLICY =
    StyleKeyRegistry.getRegistry().createKey
      ( "page-policy", false, false, StyleKey.COUNTERS );
  public static final StyleKey ORPHANS =
    StyleKeyRegistry.getRegistry().createKey
      ( "orphans", false, false, StyleKey.BLOCK_ELEMENTS );
  public static final StyleKey WIDOWS =
    StyleKeyRegistry.getRegistry().createKey
      ( "widows", false, false, StyleKey.BLOCK_ELEMENTS );
  public static final StyleKey IMAGE_ORIENTATION =
    StyleKeyRegistry.getRegistry().createKey
      ( "image-orientation", false, false, StyleKey.All_ELEMENTS );

  private PageStyleKeys() {
  }
}
