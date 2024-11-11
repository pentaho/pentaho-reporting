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


package org.pentaho.reporting.libraries.css.resolver.values.computed.line;

import org.pentaho.reporting.libraries.css.keys.line.LineStackingStrategy;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class LineStackingStrategyResolveHandler extends ConstantsResolveHandler {
  public LineStackingStrategyResolveHandler() {
    addNormalizeValue( LineStackingStrategy.BLOCK_LINE_HEIGHT );
    addNormalizeValue( LineStackingStrategy.GRID_HEIGHT );
    addNormalizeValue( LineStackingStrategy.INLINE_LINE_HEIGHT );
    addNormalizeValue( LineStackingStrategy.MAX_LINE_HEIGHT );
    setFallback( LineStackingStrategy.INLINE_LINE_HEIGHT );
  }

}
