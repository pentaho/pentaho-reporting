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

import org.pentaho.reporting.libraries.css.keys.text.TextJustify;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 14:54:02
 *
 * @author Thomas Morgner
 */
public class TextJustifyResolveHandler extends ConstantsResolveHandler {
  public TextJustifyResolveHandler() {
    addNormalizeValue( TextJustify.INTER_CHARACTER );
    addNormalizeValue( TextJustify.INTER_CLUSTER );
    addNormalizeValue( TextJustify.INTER_IDEOGRAPH );
    addNormalizeValue( TextJustify.INTER_WORD );
    addNormalizeValue( TextJustify.KASHIDA );
    addNormalizeValue( TextJustify.SIZE );
    setFallback( TextJustify.INTER_WORD );
  }

}
