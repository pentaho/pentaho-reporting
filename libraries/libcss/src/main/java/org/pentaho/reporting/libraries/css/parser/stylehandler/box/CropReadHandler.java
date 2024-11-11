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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSRectangleType;
import org.pentaho.reporting.libraries.css.values.CSSRectangleValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 15:36:05
 *
 * @author Thomas Morgner
 */
public class CropReadHandler implements CSSValueReadHandler {
  public CropReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      final String stringValue = value.getStringValue();
      if ( stringValue.equalsIgnoreCase( "auto" ) ||
        stringValue.equalsIgnoreCase( "none" ) ) {
        return CSSAutoValue.getInstance();
      }
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION ) {
      if ( value.getFunctionName().equalsIgnoreCase( "inset-rect" ) ) {
        return getRectangle( CSSRectangleType.INSET_RECT, value.getParameters() );
      }
      return null;
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_RECT_FUNCTION ) {
      return getRectangle( CSSRectangleType.RECT, value.getParameters() );
    }
    return null;
  }


  private static CSSRectangleValue getRectangle
    ( CSSRectangleType type, LexicalUnit value ) {
    final CSSNumericValue[] list = new CSSNumericValue[ 4 ];
    for ( int index = 0; index < 4; index++ ) {
      if ( value == null ) {
        return null;
      }
      CSSNumericValue nval = CSSValueFactory.createLengthValue( value );
      if ( nval != null ) {
        list[ index ] = nval;
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
        list[ index ] = CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, value.getFloatValue() );
      } else {
        return null;
      }
      value = CSSValueFactory.parseComma( value );
    }

    return new CSSRectangleValue( type, list[ 0 ], list[ 1 ], list[ 2 ], list[ 3 ] );
  }

}
