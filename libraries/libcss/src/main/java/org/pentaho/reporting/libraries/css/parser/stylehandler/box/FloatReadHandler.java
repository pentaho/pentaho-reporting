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

import org.pentaho.reporting.libraries.css.keys.box.Floating;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 15:52:18
 *
 * @author Thomas Morgner
 */
public class FloatReadHandler extends OneOfConstantsReadHandler {
  public FloatReadHandler() {
    super( false );
    addValue( Floating.BOTTOM );
    addValue( Floating.END );
    addValue( Floating.INSIDE );
    addValue( Floating.LEFT );
    addValue( Floating.NONE );
    addValue( Floating.OUTSIDE );
    addValue( Floating.RIGHT );
    addValue( Floating.START );
    addValue( Floating.TOP );
    addValue( Floating.IN_COLUMN );
    addValue( Floating.MID_COLUMN );
  }
}
