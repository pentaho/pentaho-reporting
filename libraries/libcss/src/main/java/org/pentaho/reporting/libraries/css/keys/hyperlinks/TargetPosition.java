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


package org.pentaho.reporting.libraries.css.keys.hyperlinks;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 17:30:43
 *
 * @author Thomas Morgner
 */
public class TargetPosition {
  public static final CSSConstant ABOVE = new CSSConstant(
    "above" );
  public static final CSSConstant BEHIND = new CSSConstant(
    "behind" );
  public static final CSSConstant FRONT = new CSSConstant(
    "front" );
  public static final CSSConstant BACK = new CSSConstant(
    "back" );

  private TargetPosition() {
  }
}
