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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.LineGridMode;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 03.12.2005, 19:49:08
 *
 * @author Thomas Morgner
 */
public class LineGridModeReadHandler extends OneOfConstantsReadHandler {
  public LineGridModeReadHandler() {
    super( false );
    addValue( LineGridMode.ALL );
    addValue( LineGridMode.IDEOGRAPH );
    addValue( LineGridMode.NONE );
  }
}
