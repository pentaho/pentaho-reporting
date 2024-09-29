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
