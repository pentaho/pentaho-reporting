/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
