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


package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.AlignmentBaseline;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 18:12:27
 *
 * @author Thomas Morgner
 */
public class AlignmentBaselineReadHandler extends OneOfConstantsReadHandler {
  public AlignmentBaselineReadHandler() {
    super( true );
    addValue( AlignmentBaseline.AFTER_EDGE );
    addValue( AlignmentBaseline.ALPHABETIC );
    addValue( AlignmentBaseline.CENTRAL );
    addValue( AlignmentBaseline.HANGING );
    addValue( AlignmentBaseline.IDEOGRAPHIC );
    addValue( AlignmentBaseline.MATHEMATICAL );
    addValue( AlignmentBaseline.MIDDLE );
    addValue( AlignmentBaseline.BEFORE_EDGE );
    addValue( AlignmentBaseline.TEXT_AFTER_EDGE );
    addValue( AlignmentBaseline.TEXT_BEFORE_EDGE );
    addValue( AlignmentBaseline.USE_SCRIPT );
    addValue( AlignmentBaseline.BASELINE );
  }
}
