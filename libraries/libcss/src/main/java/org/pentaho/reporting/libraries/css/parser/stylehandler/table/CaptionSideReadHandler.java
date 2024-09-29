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


package org.pentaho.reporting.libraries.css.parser.stylehandler.table;

import org.pentaho.reporting.libraries.css.keys.table.CaptionSide;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 18.07.2006, 19:00:10
 *
 * @author Thomas Morgner
 */
public class CaptionSideReadHandler extends OneOfConstantsReadHandler {
  public CaptionSideReadHandler() {
    super( false );
    addValue( CaptionSide.BOTTOM );
    addValue( CaptionSide.TOP );
  }
}
