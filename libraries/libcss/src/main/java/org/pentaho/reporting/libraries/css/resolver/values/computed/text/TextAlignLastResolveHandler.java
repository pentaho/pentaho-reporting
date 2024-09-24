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

package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.keys.text.TextAlign;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 14:17:42
 *
 * @author Thomas Morgner
 */
public class TextAlignLastResolveHandler extends ConstantsResolveHandler {
  public TextAlignLastResolveHandler() {
    addNormalizeValue( TextAlign.CENTER );
    addNormalizeValue( TextAlign.END );
    addNormalizeValue( TextAlign.JUSTIFY );
    addNormalizeValue( TextAlign.LEFT );
    addNormalizeValue( TextAlign.RIGHT );
    addNormalizeValue( TextAlign.START );
    setFallback( TextAlign.START );
  }
}
