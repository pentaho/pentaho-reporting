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
