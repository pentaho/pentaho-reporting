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
