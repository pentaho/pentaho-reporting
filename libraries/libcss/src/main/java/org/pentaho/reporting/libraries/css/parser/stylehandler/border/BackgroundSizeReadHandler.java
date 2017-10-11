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

package org.pentaho.reporting.libraries.css.parser.stylehandler.border;

import org.pentaho.reporting.libraries.css.keys.border.BackgroundSize;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 26.11.2005, 18:29:10
 *
 * @author Thomas Morgner
 */
public class BackgroundSizeReadHandler implements CSSValueReadHandler {

  public BackgroundSizeReadHandler() {
  }

  private CSSValueList createList( final CSSValue first,
                                   final CSSValue second,
                                   final CSSValue third ) {
    return new CSSValueList( new CSSValue[] { first, second, third } );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    ArrayList values = new ArrayList();

    while ( value != null ) {
      CSSValue firstValue;
      if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
        if ( value.getStringValue().equalsIgnoreCase( "round" ) ) {
          values.add( createList( CSSAutoValue.getInstance(),
            CSSAutoValue.getInstance(),
            BackgroundSize.ROUND ) );

          value = CSSValueFactory.parseComma( value );
          continue;
        }

        if ( value.getStringValue().equalsIgnoreCase( "auto" ) ) {
          firstValue = CSSAutoValue.getInstance();
        } else {
          return null;
        }
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
        firstValue = CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, value.getFloatValue() );
      } else {
        firstValue = CSSValueFactory.createLengthValue( value );
        if ( firstValue == null ) {
          return null;
        }
      }

      value = value.getNextLexicalUnit();
      if ( value == null ) {
        values.add( createList( firstValue,
          CSSAutoValue.getInstance(),
          BackgroundSize.ROUND ) );
        continue;
      }

      CSSValue secondValue;
      if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
        if ( value.getStringValue().equalsIgnoreCase( "round" ) ) {
          values.add( createList( firstValue,
            CSSAutoValue.getInstance(),
            BackgroundSize.ROUND ) );
          value = CSSValueFactory.parseComma( value );
          continue;
        } else if ( value.getStringValue().equalsIgnoreCase( "auto" ) ) {
          secondValue = CSSAutoValue.getInstance();
        } else {
          return null;
        }
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_OPERATOR_COMMA ) {
        values.add( createList( firstValue,
          CSSAutoValue.getInstance(),
          BackgroundSize.ROUND ) );
        value = value.getNextLexicalUnit();
        continue;
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
        secondValue = CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, value.getFloatValue() );
      } else {
        secondValue = CSSValueFactory.createLengthValue( value );
        if ( secondValue == null ) {
          return null;
        }
      }

      value = value.getNextLexicalUnit();
      if ( value == null ) {
        values.add( createList( firstValue,
          secondValue,
          BackgroundSize.NO_ROUND ) );
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_OPERATOR_COMMA ) {
        values.add( createList( firstValue,
          secondValue,
          BackgroundSize.NO_ROUND ) );
        value = value.getNextLexicalUnit();
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
        if ( value.getStringValue().equalsIgnoreCase( "round" ) == false ) {
          return null;
        }
        values.add( createList( firstValue,
          secondValue,
          BackgroundSize.ROUND ) );
        value = CSSValueFactory.parseComma( value );
      } else {
        return null;
      }
    }

    return new CSSValueList( values );
  }

}
