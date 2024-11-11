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

import org.pentaho.reporting.libraries.css.keys.line.TextHeight;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class TextHeightResolveHandler extends ConstantsResolveHandler {
  public TextHeightResolveHandler() {
    addNormalizeValue( TextHeight.FONT_SIZE );
    addNormalizeValue( TextHeight.MAX_SIZE );
    addNormalizeValue( TextHeight.TEXT_SIZE );
    setFallback( TextHeight.FONT_SIZE );
  }
}
