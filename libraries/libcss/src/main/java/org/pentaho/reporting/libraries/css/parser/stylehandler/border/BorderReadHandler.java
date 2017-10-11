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
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.color.ColorReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 27.11.2005, 19:52:12
 *
 * @author Thomas Morgner
 */
public class BorderReadHandler extends BorderStyleReadHandler
  implements CSSCompoundValueReadHandler {
  public BorderReadHandler() {
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final CSSValue width = CSSValueFactory.createLengthValue( unit );
    if ( width != null ) {
      unit = unit.getNextLexicalUnit();
    }

    final CSSConstant style;
    if ( unit != null ) {
      style = (CSSConstant) lookupValue( unit );
      if ( style != null ) {
        unit = unit.getNextLexicalUnit();
      }
    } else {
      style = null;
    }

    final CSSValue color;
    if ( unit != null ) {
      color = ColorReadHandler.createColorValue( unit );
    } else {
      color = null;
    }

    final Map map = new HashMap();
    if ( width != null ) {
      map.put( BorderStyleKeys.BORDER_TOP_WIDTH, width );
      map.put( BorderStyleKeys.BORDER_LEFT_WIDTH, width );
      map.put( BorderStyleKeys.BORDER_BOTTOM_WIDTH, width );
      map.put( BorderStyleKeys.BORDER_RIGHT_WIDTH, width );
    }
    if ( style != null ) {
      map.put( BorderStyleKeys.BORDER_TOP_STYLE, style );
      map.put( BorderStyleKeys.BORDER_LEFT_STYLE, style );
      map.put( BorderStyleKeys.BORDER_BOTTOM_STYLE, style );
      map.put( BorderStyleKeys.BORDER_RIGHT_STYLE, style );
    }
    if ( color != null ) {
      map.put( BorderStyleKeys.BORDER_TOP_COLOR, color );
      map.put( BorderStyleKeys.BORDER_LEFT_COLOR, color );
      map.put( BorderStyleKeys.BORDER_BOTTOM_COLOR, color );
      map.put( BorderStyleKeys.BORDER_RIGHT_COLOR, color );
    }
    return map;
  }
}
