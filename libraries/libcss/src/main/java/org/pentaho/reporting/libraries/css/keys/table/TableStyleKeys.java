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
