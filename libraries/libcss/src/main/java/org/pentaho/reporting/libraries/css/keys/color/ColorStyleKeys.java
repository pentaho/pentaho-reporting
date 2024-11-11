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


package org.pentaho.reporting.libraries.css.keys.color;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 30.10.2005, 18:47:30
 *
 * @author Thomas Morgner
 */
public final class ColorStyleKeys {
  public static final StyleKey COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "color", false, true, StyleKey.ALWAYS );

  /**
   * Not sure whether we can implement this one. It is a post-processing operation, and may or may not be supported by
   * the output target.
   */
  public static final StyleKey OPACITY =
    StyleKeyRegistry.getRegistry().createKey
      ( "opacity", false, false, StyleKey.ALWAYS );

  /**
   * For now, we do not care about color profiles. This might have to do with me being clueless about the topic, but
   * also with the cost vs. usefullness calculation involved.
   */
  public static final StyleKey COLOR_PROFILE =
    StyleKeyRegistry.getRegistry().createKey
      ( "color-profile", false, true, StyleKey.ALWAYS );
  public static final StyleKey RENDERING_INTENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "rendering-intent", false, true, StyleKey.ALWAYS );

  private ColorStyleKeys() {
  }
}
