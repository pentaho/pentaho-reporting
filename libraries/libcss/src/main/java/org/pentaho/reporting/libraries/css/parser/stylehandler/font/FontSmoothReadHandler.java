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


package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontSmooth;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 17:28:44
 *
 * @author Thomas Morgner
 */
public class FontSmoothReadHandler extends OneOfConstantsReadHandler {
  public FontSmoothReadHandler() {
    super( true );
    addValue( FontSmooth.ALWAYS );
    addValue( FontSmooth.NEVER );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    final CSSValue cssValue = super.lookupValue( value );
    if ( cssValue != null ) {
      return cssValue;
    }

    final CSSValue number = CSSValueFactory.createNumericValue( value );
    if ( number != null ) {
      return number;
    }
    return CSSValueFactory.createLengthValue( value );
  }
}
