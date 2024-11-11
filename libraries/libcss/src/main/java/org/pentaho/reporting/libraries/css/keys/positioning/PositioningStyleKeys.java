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
