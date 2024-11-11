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

import org.pentaho.reporting.libraries.css.keys.text.TextAutoSpace;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.stylehandler.ListOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 02.12.2005, 19:47:48
 *
 * @author Thomas Morgner
 */
public class TextAutoSpaceReadHandler extends ListOfConstantsReadHandler {
  public TextAutoSpaceReadHandler() {
    super( 4, false, true );
    addValue( TextAutoSpace.IDEOGRAPH_ALPHA );
    addValue( TextAutoSpace.IDEOGRAPH_NUMERIC );
    addValue( TextAutoSpace.IDEOGRAPH_PARENTHESIS );
    addValue( TextAutoSpace.IDEOGRAPH_SPACE );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "none" ) ) {
        return new CSSValueList( new CSSValue[] { TextAutoSpace.NONE } );
      }
    }
    return super.createValue( name, value );
  }
}
