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

import org.pentaho.reporting.libraries.css.keys.box.DisplayModel;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 27.11.2005, 20:46:54
 *
 * @author Thomas Morgner
 */
public class DisplayModelReadHandler extends OneOfConstantsReadHandler {
  public DisplayModelReadHandler() {
    super( false );
    addValue( DisplayModel.BLOCK_INSIDE );
    addValue( DisplayModel.INLINE_INSIDE );
    addValue( DisplayModel.RUBY );
    addValue( DisplayModel.TABLE );
    addValue( DisplayModel.CANVAS );
  }
}
