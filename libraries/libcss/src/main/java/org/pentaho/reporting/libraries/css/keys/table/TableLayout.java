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



package org.pentaho.reporting.libraries.css.keys.table;

import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 18.07.2006, 19:04:01
 *
 * @author Thomas Morgner
 */
public class TableLayout {
  public static final CSSConstant FIXED = new CSSConstant( "fixed" );
  public static final CSSAutoValue AUTO = CSSAutoValue.getInstance();

  private TableLayout() {
  }
}
