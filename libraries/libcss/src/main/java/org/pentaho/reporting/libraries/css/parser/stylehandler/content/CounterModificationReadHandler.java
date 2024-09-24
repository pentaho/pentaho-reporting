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

package org.pentaho.reporting.libraries.css.parser.stylehandler.content;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Handles both the counter-increment and the counter-reset
 *
 * @author Thomas Morgner
 */
public class CounterModificationReadHandler implements CSSValueReadHandler {
  public static final CSSNumericValue ZERO =
    CSSNumericValue.createValue( CSSNumericType.NUMBER, 0 );

  public CounterModificationReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    final String mayBeNone = value.getStringValue();
    if ( "none".equalsIgnoreCase( mayBeNone ) ) {
      return new CSSConstant( "none" );
    }

    final ArrayList counterSpecs = new ArrayList();
    while ( value != null ) {
      if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
        return null;
      }
      final String identifier = value.getStringValue();
      value = value.getNextLexicalUnit();
      CSSValue counterValue = ZERO;
      if ( value != null ) {
        if ( value.getLexicalUnitType() == LexicalUnit.SAC_INTEGER ) {
          counterValue = CSSNumericValue.createValue
            ( CSSNumericType.NUMBER, value.getIntegerValue() );
          value = value.getNextLexicalUnit();
        } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_ATTR ) {
          counterValue = CSSValueFactory.parseAttrFunction( value );
          value = value.getNextLexicalUnit();
        } else if ( CSSValueFactory.isFunctionValue( value ) ) {
          counterValue = CSSValueFactory.parseFunction( value );
          value = value.getNextLexicalUnit();
        }
      }
      counterSpecs.add( new CSSValuePair
        ( new CSSConstant( identifier ), counterValue ) );
    }

    return new CSSValueList( counterSpecs );
  }
}
