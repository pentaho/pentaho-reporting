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


package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 19:34:41
 *
 * @author Thomas Morgner
 */
public class BackgroundBreak {
  public static final CSSConstant BOUNDING_BOX = new CSSConstant(
    "bounding-box" );
  public static final CSSConstant EACH_BOX = new CSSConstant(
    "each-box" );
  public static final CSSConstant CONTINUOUS = new CSSConstant(
    "continuous" );

  private BackgroundBreak() {
  }
}
