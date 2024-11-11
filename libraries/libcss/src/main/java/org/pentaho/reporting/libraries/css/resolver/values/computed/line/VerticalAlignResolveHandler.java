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

import org.pentaho.reporting.libraries.css.keys.line.VerticalAlign;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class VerticalAlignResolveHandler extends ConstantsResolveHandler {
  public VerticalAlignResolveHandler() {
    addNormalizeValue( VerticalAlign.BASELINE );
    addNormalizeValue( VerticalAlign.BOTTOM );
    addNormalizeValue( VerticalAlign.CENTRAL );
    addNormalizeValue( VerticalAlign.MIDDLE );
    addNormalizeValue( VerticalAlign.SUB );
    addNormalizeValue( VerticalAlign.SUPER );
    addNormalizeValue( VerticalAlign.TEXT_BOTTOM );
    addNormalizeValue( VerticalAlign.TEXT_TOP );
    addNormalizeValue( VerticalAlign.TOP );
    // we do not detect scripts right now ...
    setFallback( VerticalAlign.BASELINE );
  }

}
