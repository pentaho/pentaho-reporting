/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.parser.stylehandler.hyperlinks;

import org.pentaho.reporting.libraries.css.keys.hyperlinks.TargetPosition;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 19:32:18
 *
 * @author Thomas Morgner
 */
public class TargetPositionReadHandler extends OneOfConstantsReadHandler {
  public TargetPositionReadHandler() {
    super( false );
    addValue( TargetPosition.ABOVE );
    addValue( TargetPosition.BACK );
    addValue( TargetPosition.BEHIND );
    addValue( TargetPosition.FRONT );
  }
}
