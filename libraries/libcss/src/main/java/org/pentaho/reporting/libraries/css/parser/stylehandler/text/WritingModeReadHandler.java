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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.BlockProgression;
import org.pentaho.reporting.libraries.css.keys.text.Direction;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSInheritValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 02.12.2005, 17:38:27
 *
 * @author Thomas Morgner
 */
public class WritingModeReadHandler implements CSSCompoundValueReadHandler {
  public WritingModeReadHandler() {
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    if ( unit.getLexicalUnitType() == LexicalUnit.SAC_INHERIT ) {
      Map map = new HashMap();
      map.put( TextStyleKeys.DIRECTION, CSSInheritValue.getInstance() );
      map.put( TextStyleKeys.BLOCK_PROGRESSION, CSSInheritValue.getInstance() );
      return map;
    }

    if ( unit.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }

    CSSValue direction;
    CSSValue blockProgression;
    final String strValue = unit.getStringValue();
    // lr-tb | rl-tb | tb-rl | tb-lr
    if ( strValue.equalsIgnoreCase( "lr-tb" ) ) {
      direction = Direction.LTR;
      blockProgression = BlockProgression.TB;
    } else if ( strValue.equalsIgnoreCase( "rl-tb" ) ) {
      direction = Direction.RTL;
      blockProgression = BlockProgression.TB;
    } else if ( strValue.equalsIgnoreCase( "tb-rl" ) ) {
      direction = Direction.LTR;
      blockProgression = BlockProgression.RL;
    } else if ( strValue.equalsIgnoreCase( "tb-lr" ) ) {
      direction = Direction.RTL;
      blockProgression = BlockProgression.LR;
    } else {
      return null;
    }

    Map map = new HashMap();
    map.put( TextStyleKeys.DIRECTION, direction );
    map.put( TextStyleKeys.BLOCK_PROGRESSION, blockProgression );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] { TextStyleKeys.DIRECTION, TextStyleKeys.BLOCK_PROGRESSION };
  }
}
