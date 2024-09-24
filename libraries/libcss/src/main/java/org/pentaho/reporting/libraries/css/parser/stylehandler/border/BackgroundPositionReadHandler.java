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

package org.pentaho.reporting.libraries.css.parser.stylehandler.border;

import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.ListOfPairReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 26.11.2005, 18:29:10
 *
 * @author Thomas Morgner
 */
public class BackgroundPositionReadHandler extends ListOfPairReadHandler {
  public static final CSSNumericValue CENTER =
    CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 50 );
  public static final CSSNumericValue TOP =
    CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 0 );
  public static final CSSNumericValue LEFT =
    CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 0 );
  public static final CSSNumericValue BOTTOM =
    CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 100 );
  public static final CSSNumericValue RIGHT =
    CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, 100 );

  public BackgroundPositionReadHandler() {
  }

  protected CSSValue parseFirstPosition( final LexicalUnit value ) {
    if ( value == null ) {
      return null;
    }

    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( "left".equalsIgnoreCase( value.getStringValue() ) ) {
        return LEFT;
      } else if ( "center".equalsIgnoreCase( value.getStringValue() ) ) {
        return CENTER;
      } else if ( "right".equalsIgnoreCase( value.getStringValue() ) ) {
        return RIGHT;
      } else if ( "top".equalsIgnoreCase( value.getStringValue() ) ) {
        return TOP;
      } else if ( "bottom".equalsIgnoreCase( value.getStringValue() ) ) {
        return BOTTOM;
      }

      // ignore this rule.
      return null;
    }

    if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE,
        value.getFloatValue() );
    }
    if ( CSSValueFactory.isLengthValue( value ) ) {
      return CSSValueFactory.createLengthValue( value );
    }
    // contains errors, we ignore this rule.
    return null;
  }

  protected void addToResultList( ArrayList values,
                                  CSSValue firstPosition,
                                  CSSValue secondPosition ) {
    if ( firstPosition == TOP || firstPosition == BOTTOM ) {
      super.addToResultList( values, secondPosition, firstPosition );
    } else if ( secondPosition == LEFT || secondPosition == RIGHT ) {
      super.addToResultList( values, secondPosition, firstPosition );
    } else {
      super.addToResultList( values, firstPosition, secondPosition );
    }
  }

  protected CSSValue parseSecondPosition( final LexicalUnit value,
                                          final CSSValue firstValue ) {
    if ( value == null ) {
      return CENTER;
    }
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( "left".equalsIgnoreCase( value.getStringValue() ) ) {
        return LEFT;
      } else if ( "center".equalsIgnoreCase( value.getStringValue() ) ) {
        return CENTER;
      } else if ( "right".equalsIgnoreCase( value.getStringValue() ) ) {
        return RIGHT;
      } else if ( "top".equalsIgnoreCase( value.getStringValue() ) ) {
        return TOP;
      } else if ( "bottom".equalsIgnoreCase( value.getStringValue() ) ) {
        return BOTTOM;
      }
      return null; // ignore this rule, it contains errors.
    }
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE,
        value.getFloatValue() );
    } else if ( CSSValueFactory.isLengthValue( value ) ) {
      return CSSValueFactory.createLengthValue( value );
    }
    return CENTER;
  }
}
