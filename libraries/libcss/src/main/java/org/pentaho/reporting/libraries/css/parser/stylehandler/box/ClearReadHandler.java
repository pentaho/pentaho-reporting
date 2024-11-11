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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.keys.box.Clear;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 15:54:25
 *
 * @author Thomas Morgner
 */
public class ClearReadHandler extends OneOfConstantsReadHandler {
  public ClearReadHandler() {
    super( false );
    addValue( Clear.BOTH );
    addValue( Clear.BOTTOM );
    addValue( Clear.END );
    addValue( Clear.INSIDE );
    addValue( Clear.LEFT );
    addValue( Clear.NONE );
    addValue( Clear.OUTSIDE );
    addValue( Clear.RIGHT );
    addValue( Clear.START );
    addValue( Clear.TOP );
  }
}
