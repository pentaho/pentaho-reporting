/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
