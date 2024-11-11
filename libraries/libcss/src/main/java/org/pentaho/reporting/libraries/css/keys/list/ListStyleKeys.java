/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.keys.list;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 01.12.2005, 19:15:04
 *
 * @author Thomas Morgner
 */
public class ListStyleKeys {
  public static final StyleKey LIST_STYLE_IMAGE =
    StyleKeyRegistry.getRegistry().createKey
      ( "list-style-image", false, false,
        StyleKey.DOM_ELEMENTS | StyleKey.PSEUDO_MARKER );
  public static final StyleKey LIST_STYLE_TYPE =
    StyleKeyRegistry.getRegistry().createKey
      ( "list-style-type", false, false,
        StyleKey.DOM_ELEMENTS | StyleKey.PSEUDO_MARKER );
  public static final StyleKey LIST_STYLE_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "list-style-position", false, false,
        StyleKey.DOM_ELEMENTS | StyleKey.PSEUDO_MARKER );


  private ListStyleKeys() {
  }
}
