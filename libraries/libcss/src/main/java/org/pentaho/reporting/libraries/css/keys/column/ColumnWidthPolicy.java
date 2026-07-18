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



package org.pentaho.reporting.libraries.css.keys.column;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 03.12.2005, 21:11:06
 *
 * @author Thomas Morgner
 */
public class ColumnWidthPolicy {
  public static final CSSConstant FLEXIBLE =
    new CSSConstant( "flexible" );
  public static final CSSConstant STRICT =
    new CSSConstant( "strict" );

  private ColumnWidthPolicy() {
  }
}
