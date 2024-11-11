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


package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.DominantBaseline;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 18:12:27
 *
 * @author Thomas Morgner
 */
public class DominantBaselineReadHandler extends OneOfConstantsReadHandler {
  public DominantBaselineReadHandler() {
    super( true );
    addValue( DominantBaseline.ALPHABETIC );
    addValue( DominantBaseline.CENTRAL );
    addValue( DominantBaseline.HANGING );
    addValue( DominantBaseline.IDEOGRAPHIC );
    addValue( DominantBaseline.MATHEMATICAL );
    addValue( DominantBaseline.MIDDLE );
    addValue( DominantBaseline.NO_CHANGE );
    addValue( DominantBaseline.RESET_SIZE );
    addValue( DominantBaseline.TEXT_AFTER_EDGE );
    addValue( DominantBaseline.TEXT_BEFORE_EDGE );
    addValue( DominantBaseline.USE_SCRIPT );
  }
}
