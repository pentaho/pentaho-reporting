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


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextAlign;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 19:47:58
 *
 * @author Thomas Morgner
 */
public class TextAlignReadHandler extends OneOfConstantsReadHandler {
  public TextAlignReadHandler() {
    super( true );
    addValue( TextAlign.CENTER );
    addValue( TextAlign.END );
    addValue( TextAlign.JUSTIFY );
    addValue( TextAlign.LEFT );
    addValue( TextAlign.RIGHT );
    addValue( TextAlign.START );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ) {
      return new CSSStringValue( CSSStringType.STRING, value.getStringValue() );
    }

    return super.lookupValue( value );
  }
}
