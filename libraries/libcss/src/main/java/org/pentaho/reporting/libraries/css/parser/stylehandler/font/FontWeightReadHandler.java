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


package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontWeight;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 16:44:30
 *
 * @author Thomas Morgner
 */
public class FontWeightReadHandler implements CSSValueReadHandler {
  public FontWeightReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "bolder" ) ) {
        return FontWeight.BOLDER;
      }
      if ( value.getStringValue().equalsIgnoreCase( "bold" ) ) {
        return FontWeight.BOLD;
      }
      if ( value.getStringValue().equalsIgnoreCase( "lighter" ) ) {
        return FontWeight.LIGHTER;
      }
      if ( value.getStringValue().equalsIgnoreCase( "normal" ) ) {
        return FontWeight.NORMAL;
      }
    }
    return CSSValueFactory.createNumericValue( value );
  }
}
