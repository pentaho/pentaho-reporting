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

import org.pentaho.reporting.libraries.css.keys.box.FloatingType;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 15:52:18
 *
 * @author Thomas Morgner
 */
public class FloatTypeReadHandler extends OneOfConstantsReadHandler {
  public FloatTypeReadHandler() {
    super( false );
    addValue( FloatingType.BOX );
    addValue( FloatingType.SHAPE );
  }
}
