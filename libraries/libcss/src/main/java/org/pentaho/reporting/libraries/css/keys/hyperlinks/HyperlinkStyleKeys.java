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


package org.pentaho.reporting.libraries.css.keys.hyperlinks;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 24.11.2005, 17:25:02
 *
 * @author Thomas Morgner
 */
public class HyperlinkStyleKeys {
  public static final StyleKey TARGET_NAME =
    StyleKeyRegistry.getRegistry().createKey
      ( "target-name", false, false, StyleKey.All_ELEMENTS );
  public static final StyleKey TARGET_NEW =
    StyleKeyRegistry.getRegistry().createKey
      ( "target-new", false, false, StyleKey.All_ELEMENTS );
  public static final StyleKey TARGET_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "target-position", false, false, StyleKey.All_ELEMENTS );

  //// This stuff is output specific and therefore must be declared in the processor itself
  //// Such style-keys will be implemented in the Classic-Engine itself
  //  /**
  //   * This is a libLayout extension to allow a document independent
  //   * link specification. It is up to the output target to support that.
  //   * <p/>
  //   * Example style definition:  a { -x-pentaho-css-href-target: attr("href"); }
  //   */
  //  public static final StyleKey HREF_TARGET =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-href-target", true, false, StyleKey.All_ELEMENTS);
  //  /**
  //   * This is a libLayout extension to allow a document independent
  //   * anchor specifications. It is up to the output target to support that.
  //   * <p/>
  //   * Example style definition:  a { x-href-anchor: attr("name"); }
  //   */
  //  public static final StyleKey ANCHOR =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-href-anchor", true, false, StyleKey.All_ELEMENTS);

  private HyperlinkStyleKeys() {
  }
}
