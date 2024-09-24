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
