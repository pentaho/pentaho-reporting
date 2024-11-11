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

import org.pentaho.reporting.libraries.css.keys.line.LineStackingStrategy;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 18:02:53
 *
 * @author Thomas Morgner
 */
public class LineStackingStrategyReadHandler extends OneOfConstantsReadHandler {
  public LineStackingStrategyReadHandler() {
    super( true );
    addValue( LineStackingStrategy.BLOCK_LINE_HEIGHT );
    addValue( LineStackingStrategy.GRID_HEIGHT );
    addValue( LineStackingStrategy.INLINE_LINE_HEIGHT );
    addValue( LineStackingStrategy.MAX_LINE_HEIGHT );
  }
}
