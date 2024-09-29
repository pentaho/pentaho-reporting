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


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 02.12.2005, 19:13:31
 *
 * @author Thomas Morgner
 */
public class TextIndentReadHandler implements CSSValueReadHandler {
  public TextIndentReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {

    CSSValue cssvalue = null;
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      cssvalue = CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, value.getFloatValue() );
    } else {
      cssvalue = CSSValueFactory.createLengthValue( value );
    }

    value = value.getNextLexicalUnit();
    if ( value != null ) {
      if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
        return null;
      }
      if ( value.getStringValue().equalsIgnoreCase( "hanging" ) ) {
        return new CSSValueList( new CSSValue[] { cssvalue, new CSSConstant( "hanging" ) } );
      } else {
        return null;
      }
    }

    return new CSSValueList( new CSSValue[] { cssvalue } );
  }
}
