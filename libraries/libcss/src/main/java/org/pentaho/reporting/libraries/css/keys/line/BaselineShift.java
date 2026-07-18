/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 17:06:06
 *
 * @author Thomas Morgner
 */
public class BaselineShift {
  public static final CSSConstant BASELINE =
    new CSSConstant( "baseline" );
  public static final CSSConstant SUB =
    new CSSConstant( "sub" );
  public static final CSSConstant SUPER =
    new CSSConstant( "super" );

  private BaselineShift() {
  }
}
