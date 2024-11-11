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


package org.pentaho.reporting.libraries.css.parser.stylehandler.column;

import org.pentaho.reporting.libraries.css.keys.column.ColumnWidthPolicy;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 08.12.2005, 17:33:13
 *
 * @author Thomas Morgner
 */
public class ColumnWidthPolicyReadHandler extends OneOfConstantsReadHandler {
  public ColumnWidthPolicyReadHandler() {
    super( false );
    addValue( ColumnWidthPolicy.FLEXIBLE );
    addValue( ColumnWidthPolicy.STRICT );
  }
}
