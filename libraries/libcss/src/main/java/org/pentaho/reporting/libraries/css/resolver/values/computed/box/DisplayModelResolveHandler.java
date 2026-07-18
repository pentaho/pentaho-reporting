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



package org.pentaho.reporting.libraries.css.resolver.values.computed.box;

import org.pentaho.reporting.libraries.css.keys.box.DisplayModel;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class DisplayModelResolveHandler extends ConstantsResolveHandler {
  public DisplayModelResolveHandler() {
    addNormalizeValue( DisplayModel.BLOCK_INSIDE );
    addNormalizeValue( DisplayModel.INLINE_INSIDE );
    addNormalizeValue( DisplayModel.RUBY );
    addNormalizeValue( DisplayModel.TABLE );
    setFallback( DisplayModel.INLINE_INSIDE );
  }

}
