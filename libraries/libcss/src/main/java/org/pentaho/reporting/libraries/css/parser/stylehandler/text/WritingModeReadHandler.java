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
