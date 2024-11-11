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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.keys.box.BoxStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractWidthReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 27.11.2005, 19:07:11
 *
 * @author Thomas Morgner
 */
public class PaddingReadHandler extends AbstractWidthReadHandler
  implements CSSCompoundValueReadHandler {
  public PaddingReadHandler() {
    super( true, false );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final CSSValue topWidth = parseWidth( unit );
    if ( topWidth == null ) {
      return null;
    }

    unit = unit.getNextLexicalUnit();

    final CSSValue rightWidth;
    if ( unit == null ) {
      rightWidth = topWidth;
    } else {
      rightWidth = parseWidth( unit );
      if ( rightWidth == null ) {
        return null;
      }
      unit = unit.getNextLexicalUnit();
    }

    final CSSValue bottomWidth;
    if ( unit == null ) {
      bottomWidth = topWidth;
    } else {
      bottomWidth = parseWidth( unit );
      if ( bottomWidth == null ) {
        return null;
      }
      unit = unit.getNextLexicalUnit();
    }

    final CSSValue leftWidth;
    if ( unit == null ) {
      leftWidth = rightWidth;
    } else {
      leftWidth = parseWidth( unit );
      if ( leftWidth == null ) {
        return null;
      }
    }

    final Map map = new HashMap();
    map.put( BoxStyleKeys.PADDING_TOP, topWidth );
    map.put( BoxStyleKeys.PADDING_RIGHT, rightWidth );
    map.put( BoxStyleKeys.PADDING_BOTTOM, bottomWidth );
    map.put( BoxStyleKeys.PADDING_LEFT, leftWidth );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      BoxStyleKeys.PADDING_TOP,
      BoxStyleKeys.PADDING_RIGHT,
      BoxStyleKeys.PADDING_BOTTOM,
      BoxStyleKeys.PADDING_LEFT
    };
  }
}
