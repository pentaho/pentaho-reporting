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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.keys.box.FloatDisplace;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 15:54:25
 *
 * @author Thomas Morgner
 */
public class FloatDisplaceReadHandler extends OneOfConstantsReadHandler {
  public FloatDisplaceReadHandler() {
    super( false );
    addValue( FloatDisplace.BLOCK );
    addValue( FloatDisplace.BLOCK_WITHIN_PAGE );
    addValue( FloatDisplace.INDENT );
    addValue( FloatDisplace.LINE );
  }
}
