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


package org.pentaho.reporting.libraries.css.keys.canvas;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * All kind of StyleKeys needed for compatiblity with the old display model. This should be moved into the reporting
 * engine itself.
 *
 * @author Thomas Morgner
 */
public class CanvasStyleKeys {
  public static final StyleKey POSITION_X =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-reporting-x-position", true, false, StyleKey.DOM_ELEMENTS );

  public static final StyleKey POSITION_Y =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-reporting-y-position", true, true, StyleKey.DOM_ELEMENTS );

  private CanvasStyleKeys() {
  }

}
