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

package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.LineStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 28.11.2005, 18:06:12
 *
 * @author Thomas Morgner
 */
public class LineStackingReadHandler implements CSSCompoundValueReadHandler {
  private LineStackingRubyReadHandler rubyReadHandler;
  private LineStackingShiftReadHandler shiftReadHandler;
  private LineStackingStrategyReadHandler strategyReadHandler;

  public LineStackingReadHandler() {
    rubyReadHandler = new LineStackingRubyReadHandler();
    shiftReadHandler = new LineStackingShiftReadHandler();
    strategyReadHandler = new LineStackingStrategyReadHandler();
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    CSSValue rubyValue = rubyReadHandler.createValue( null, unit );
    if ( rubyValue != null ) {
      unit = unit.getNextLexicalUnit();
    }

    CSSValue shiftValue;
    if ( unit != null ) {
      shiftValue = shiftReadHandler.createValue( null, unit );
      if ( shiftValue != null ) {
        unit = unit.getNextLexicalUnit();
      }
    } else {
      shiftValue = null;
    }

    CSSValue strategy;
    if ( unit != null ) {
      strategy = strategyReadHandler.createValue( null, unit );
    } else {
      strategy = null;
    }

    final Map map = new HashMap();
    if ( rubyValue != null ) {
      map.put( LineStyleKeys.LINE_STACKING_RUBY, rubyValue );
    }
    if ( shiftValue != null ) {
      map.put( LineStyleKeys.LINE_STACKING_SHIFT, shiftValue );
    }
    if ( strategy != null ) {
      map.put( LineStyleKeys.LINE_STACKING_STRATEGY, strategy );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      LineStyleKeys.LINE_STACKING_RUBY,
      LineStyleKeys.LINE_STACKING_SHIFT,
      LineStyleKeys.LINE_STACKING_STRATEGY
    };
  }
}
