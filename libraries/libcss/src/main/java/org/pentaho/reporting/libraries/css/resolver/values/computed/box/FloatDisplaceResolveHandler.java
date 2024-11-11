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


package org.pentaho.reporting.libraries.css.resolver.values.computed.box;

import org.pentaho.reporting.libraries.css.keys.box.FloatDisplace;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class FloatDisplaceResolveHandler extends ConstantsResolveHandler {
  public FloatDisplaceResolveHandler() {
    addNormalizeValue( FloatDisplace.BLOCK );
    addNormalizeValue( FloatDisplace.BLOCK_WITHIN_PAGE );
    addNormalizeValue( FloatDisplace.INDENT );
    addNormalizeValue( FloatDisplace.LINE );
    setFallback( FloatDisplace.LINE );
  }
}
