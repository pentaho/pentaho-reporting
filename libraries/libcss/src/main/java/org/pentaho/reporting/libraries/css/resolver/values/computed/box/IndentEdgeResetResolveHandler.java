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


package org.pentaho.reporting.libraries.css.resolver.values.computed.box;

import org.pentaho.reporting.libraries.css.keys.box.IndentEdgeReset;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class IndentEdgeResetResolveHandler extends ConstantsResolveHandler {
  public IndentEdgeResetResolveHandler() {
    addNormalizeValue( IndentEdgeReset.BORDER_EDGE );
    addNormalizeValue( IndentEdgeReset.CONTENT_EDGE );
    addNormalizeValue( IndentEdgeReset.MARGIN_EDGE );
    addNormalizeValue( IndentEdgeReset.NONE );
    addNormalizeValue( IndentEdgeReset.PADDING_EDGE );
    setFallback( IndentEdgeReset.NONE );
  }

}
