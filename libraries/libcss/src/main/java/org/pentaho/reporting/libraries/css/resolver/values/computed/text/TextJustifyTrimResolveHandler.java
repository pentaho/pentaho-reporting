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

import org.pentaho.reporting.libraries.css.keys.text.TextJustifyTrim;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 14:54:02
 *
 * @author Thomas Morgner
 */
public class TextJustifyTrimResolveHandler extends ConstantsResolveHandler {
  public TextJustifyTrimResolveHandler() {
    addNormalizeValue( TextJustifyTrim.NONE );
    addNormalizeValue( TextJustifyTrim.PUNCTUATION );
    addNormalizeValue( TextJustifyTrim.PUNCTUATION_AND_KANA );
    setFallback( TextJustifyTrim.NONE );
  }

}
