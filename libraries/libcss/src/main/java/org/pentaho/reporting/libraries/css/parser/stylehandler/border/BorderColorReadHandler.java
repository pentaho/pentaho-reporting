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
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.color.ColorReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 27.11.2005, 19:07:11
 *
 * @author Thomas Morgner
 */
public class BorderColorReadHandler implements CSSValueReadHandler, CSSCompoundValueReadHandler {
  public BorderColorReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    return ColorReadHandler.createColorValue( value );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final CSSValue topColor = ColorReadHandler.createColorValue( unit );
    if ( topColor == null ) {
      return null;
    }

    unit = unit.getNextLexicalUnit();

    final CSSValue rightColor;
    if ( unit == null ) {
      rightColor = topColor;
    } else {
      rightColor = ColorReadHandler.createColorValue( unit );
      if ( rightColor == null ) {
        return null;
      }
      unit = unit.getNextLexicalUnit();
    }

    final CSSValue bottomColor;
    if ( unit == null ) {
      bottomColor = topColor;
    } else {
      bottomColor = ColorReadHandler.createColorValue( unit );
      if ( bottomColor == null ) {
        return null;
      }
      unit = unit.getNextLexicalUnit();
    }

    final CSSValue leftColor;
    if ( unit == null ) {
      leftColor = rightColor;
    } else {
      leftColor = ColorReadHandler.createColorValue( unit );
      if ( leftColor == null ) {
        return null;
      }
    }

    final Map map = new HashMap();
    map.put( BorderStyleKeys.BORDER_TOP_COLOR, topColor );
    map.put( BorderStyleKeys.BORDER_RIGHT_COLOR, rightColor );
    map.put( BorderStyleKeys.BORDER_BOTTOM_COLOR, bottomColor );
    map.put( BorderStyleKeys.BORDER_LEFT_COLOR, leftColor );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      BorderStyleKeys.BORDER_TOP_COLOR,
      BorderStyleKeys.BORDER_RIGHT_COLOR,
      BorderStyleKeys.BORDER_BOTTOM_COLOR,
      BorderStyleKeys.BORDER_LEFT_COLOR
    };
  }
}
