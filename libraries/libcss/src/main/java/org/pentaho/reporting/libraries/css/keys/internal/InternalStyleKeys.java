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

package org.pentaho.reporting.libraries.css.keys.internal;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * @author Thomas Morgner
 */
public class InternalStyleKeys {
  /**
   * To which Layouter pseudo-class does this element belong to. A pseudo-class membership is defined by an expression.
   */
  public static final StyleKey PSEUDOCLASS =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-pseudoclass", false, false, StyleKey.All_ELEMENTS );

  /**
   * Which language does the content have? This is an ISO code like 'en' maybe enriched with an country code 'en_US' and
   * variant 'en_US_native'
   */
  public static final StyleKey LANG =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-language", true, true, StyleKey.All_ELEMENTS );

  /**
   * A internal key holding the computed content. The value for the key is defined during the resolving and by all
   * means: Do not even think about touching that key without proper purification of your soul! Clueless messing around
   * with this property will kill you.
   */
  public static final StyleKey INTERNAL_CONTENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-internal-content", true, false, StyleKey.All_ELEMENTS );

  private InternalStyleKeys() {
  }
}
