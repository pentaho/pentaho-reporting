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

import org.pentaho.reporting.libraries.css.keys.border.BorderStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * This looks a bit funny, as if the standard has not been completed. THe compound property may change ...
 *
 * @author Thomas Morgner
 */
public class BorderRadiusReadHandler implements CSSValueReadHandler, CSSCompoundValueReadHandler {
  public BorderRadiusReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    CSSNumericValue firstValue = CSSValueFactory.createLengthValue( value );
    if ( firstValue == null ) {
      return null;
    }
    value = value.getNextLexicalUnit();
    CSSNumericValue secondValue;
    if ( value == null ) {
      secondValue = firstValue;
    } else {
      secondValue = CSSValueFactory.createLengthValue( value );
      if ( secondValue == null ) {
        return null;
      }
    }

    return new CSSValuePair( firstValue, secondValue );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final CSSValue value = createValue( null, unit );
    if ( value == null ) {
      return null;
    }

    final Map map = new HashMap();
    map.put( BorderStyleKeys.BORDER_TOP_RIGHT_RADIUS, value );
    map.put( BorderStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS, value );
    map.put( BorderStyleKeys.BORDER_BOTTOM_LEFT_RADIUS, value );
    map.put( BorderStyleKeys.BORDER_TOP_LEFT_RADIUS, value );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      BorderStyleKeys.BORDER_TOP_RIGHT_RADIUS,
      BorderStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS,
      BorderStyleKeys.BORDER_BOTTOM_LEFT_RADIUS,
      BorderStyleKeys.BORDER_TOP_LEFT_RADIUS
    };
  }
}
