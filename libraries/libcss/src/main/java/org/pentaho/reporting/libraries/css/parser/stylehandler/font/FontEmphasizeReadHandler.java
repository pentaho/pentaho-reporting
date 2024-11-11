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


package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 28.11.2005, 17:52:55
 *
 * @author Thomas Morgner
 */
public class FontEmphasizeReadHandler
  implements CSSCompoundValueReadHandler {
  private FontEmphasizePositionReadHandler positionReadHandler;
  private FontEmphasizeStyleReadHandler styleReadHandler;

  public FontEmphasizeReadHandler() {
    positionReadHandler = new FontEmphasizePositionReadHandler();
    styleReadHandler = new FontEmphasizeStyleReadHandler();
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    CSSValue style = styleReadHandler.createValue( null, unit );
    if ( style != null ) {
      unit = unit.getNextLexicalUnit();
    }
    CSSValue position;
    if ( unit != null ) {
      position = positionReadHandler.createValue( null, unit );
    } else {
      position = null;
    }
    final Map map = new HashMap();
    if ( position != null ) {
      map.put( FontStyleKeys.FONT_EMPHASIZE_POSITION, position );
    }
    if ( style != null ) {
      map.put( FontStyleKeys.FONT_EMPHASIZE_STYLE, style );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      FontStyleKeys.FONT_EMPHASIZE_POSITION,
      FontStyleKeys.FONT_EMPHASIZE_STYLE
    };
  }
}
