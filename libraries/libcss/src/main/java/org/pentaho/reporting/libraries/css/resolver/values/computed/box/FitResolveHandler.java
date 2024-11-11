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

import org.pentaho.reporting.libraries.css.keys.box.Fit;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class FitResolveHandler extends ConstantsResolveHandler {
  public FitResolveHandler() {
    addNormalizeValue( Fit.FILL );
    addNormalizeValue( Fit.MEET );
    addNormalizeValue( Fit.NONE );
    addNormalizeValue( Fit.SLICE );
    setFallback( Fit.FILL );
  }


}
