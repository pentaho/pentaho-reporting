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



package org.pentaho.reporting.libraries.css.parser.stylehandler.column;

import org.pentaho.reporting.libraries.css.parser.stylehandler.border.BorderWidthReadHandler;

/**
 * Creation-Date: 04.12.2005, 13:26:49
 *
 * @author Thomas Morgner
 */
public class ColumnRuleWidthReadHandler extends BorderWidthReadHandler {
  public ColumnRuleWidthReadHandler() {
    super( true, false );
  }
}
