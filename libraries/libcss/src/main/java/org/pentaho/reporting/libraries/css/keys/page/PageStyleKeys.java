/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
