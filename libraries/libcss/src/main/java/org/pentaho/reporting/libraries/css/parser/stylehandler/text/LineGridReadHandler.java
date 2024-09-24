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

import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractCompoundValueReadHandler;

/**
 * Creation-Date: 03.12.2005, 19:53:30
 *
 * @author Thomas Morgner
 */
public class LineGridReadHandler extends AbstractCompoundValueReadHandler {
  public LineGridReadHandler() {
    addHandler( TextStyleKeys.LINE_GRID_MODE, new LineGridModeReadHandler() );
    addHandler( TextStyleKeys.LINE_GRID_PROGRESSION, new LineGridProgressionReadHandler() );
  }
}
