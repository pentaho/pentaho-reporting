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

import org.pentaho.reporting.libraries.css.keys.border.BackgroundRepeat;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 27.11.2005, 18:36:29
 *
 * @author Thomas Morgner
 */
public class BackgroundRepeatReadHandler implements CSSValueReadHandler {
  public BackgroundRepeatReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    ArrayList values = new ArrayList();

    while ( value != null ) {
      if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
        return null;
      }

      final CSSConstant horizontal;
      final CSSConstant vertical;

      final String horizontalString = value.getStringValue();
      if ( horizontalString.equalsIgnoreCase( "repeat-x" ) ) {
        horizontal = BackgroundRepeat.REPEAT;
        vertical = BackgroundRepeat.NOREPEAT;
      } else if ( value.getStringValue().equalsIgnoreCase( "repeat-y" ) ) {
        horizontal = BackgroundRepeat.NOREPEAT;
        vertical = BackgroundRepeat.REPEAT;
      } else {
        horizontal = translateRepeat( horizontalString );
        if ( horizontal == null ) {
          return null;
        }

        value = value.getNextLexicalUnit();
        if ( value == null ) {
          vertical = horizontal;
        } else if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
          return null;
        } else {
          vertical = translateRepeat( value.getStringValue() );
          if ( vertical == null ) {
            return null;
          }
        }
      }

      values.add( new CSSValuePair( horizontal, vertical ) );
      value = CSSValueFactory.parseComma( value );
    }

    return new CSSValueList( values );
  }

  private CSSConstant translateRepeat( final String value ) {
    if ( value.equalsIgnoreCase( "repeat" ) ) {
      return BackgroundRepeat.REPEAT;
    }
    if ( value.equalsIgnoreCase( "no-repeat" ) ) {
      return BackgroundRepeat.NOREPEAT;
    }
    if ( value.equalsIgnoreCase( "space" ) ) {
      return BackgroundRepeat.SPACE;
    }
    return null;
  }
}
