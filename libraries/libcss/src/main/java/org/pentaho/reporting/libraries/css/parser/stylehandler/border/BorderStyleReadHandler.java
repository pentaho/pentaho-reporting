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

import org.pentaho.reporting.libraries.css.keys.border.BorderStyle;
import org.pentaho.reporting.libraries.css.keys.border.BorderStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 27.11.2005, 19:17:22
 *
 * @author Thomas Morgner
 */
public class BorderStyleReadHandler extends OneOfConstantsReadHandler
  implements CSSCompoundValueReadHandler {
  public BorderStyleReadHandler() {
    super( false );
    addValue( BorderStyle.DASHED );
    addValue( BorderStyle.DOT_DASH );
    addValue( BorderStyle.DOT_DOT_DASH );
    addValue( BorderStyle.DOTTED );
    addValue( BorderStyle.DOUBLE );
    addValue( BorderStyle.GROOVE );
    addValue( BorderStyle.HIDDEN );
    addValue( BorderStyle.INSET );
    addValue( BorderStyle.NONE );
    addValue( BorderStyle.OUTSET );
    addValue( BorderStyle.RIDGE );
    addValue( BorderStyle.SOLID );
    addValue( BorderStyle.WAVE );
  }


  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final CSSConstant topStyle = (CSSConstant) lookupValue( unit );
    if ( topStyle == null ) {
      return null;
    }

    unit = unit.getNextLexicalUnit();

    final CSSConstant rightStyle;
    if ( unit == null ) {
      rightStyle = topStyle;
    } else {
      rightStyle = (CSSConstant) lookupValue( unit );
      if ( rightStyle == null ) {
        return null;
      }
      unit = unit.getNextLexicalUnit();
    }

    final CSSConstant bottomStyle;
    if ( unit == null ) {
      bottomStyle = topStyle;
    } else {
      bottomStyle = (CSSConstant) lookupValue( unit );
      if ( bottomStyle == null ) {
        return null;
      }
      unit = unit.getNextLexicalUnit();
    }

    final CSSConstant leftStyle;
    if ( unit == null ) {
      leftStyle = rightStyle;
    } else {
      leftStyle = (CSSConstant) lookupValue( unit );
      if ( leftStyle == null ) {
        return null;
      }
    }

    final Map map = new HashMap();
    map.put( BorderStyleKeys.BORDER_TOP_STYLE, topStyle );
    map.put( BorderStyleKeys.BORDER_RIGHT_STYLE, rightStyle );
    map.put( BorderStyleKeys.BORDER_BOTTOM_STYLE, bottomStyle );
    map.put( BorderStyleKeys.BORDER_LEFT_STYLE, leftStyle );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      BorderStyleKeys.BORDER_TOP_STYLE,
      BorderStyleKeys.BORDER_RIGHT_STYLE,
      BorderStyleKeys.BORDER_BOTTOM_STYLE,
      BorderStyleKeys.BORDER_LEFT_STYLE
    };
  }
}
