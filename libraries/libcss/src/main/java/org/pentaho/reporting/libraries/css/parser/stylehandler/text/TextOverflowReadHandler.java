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

import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSInheritValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 02.12.2005, 19:36:00
 *
 * @author Thomas Morgner
 */
public class TextOverflowReadHandler implements CSSCompoundValueReadHandler {
  private TextOverflowModeReadHandler modeReadHandler;
  private TextOverflowEllipsisReadHandler ellipsisReadHandler;

  public TextOverflowReadHandler() {
    modeReadHandler = new TextOverflowModeReadHandler();
    ellipsisReadHandler = new TextOverflowEllipsisReadHandler();
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
      map.put( TextStyleKeys.TEXT_OVERFLOW_MODE, CSSInheritValue.getInstance() );
      map.put( TextStyleKeys.TEXT_OVERFLOW_ELLIPSIS, CSSInheritValue.getInstance() );
      return map;
    }


    CSSValue mode = modeReadHandler.createValue( null, unit );
    if ( mode != null ) {
      unit = unit.getNextLexicalUnit();
    }
    CSSValue ellipsis;
    if ( unit != null ) {
      ellipsis = ellipsisReadHandler.createValue( null, unit );
    } else {
      ellipsis = null;
    }
    Map map = new HashMap();
    if ( mode != null ) {
      map.put( TextStyleKeys.TEXT_OVERFLOW_MODE, mode );
    }
    if ( ellipsis != null ) {
      map.put( TextStyleKeys.TEXT_OVERFLOW_ELLIPSIS, ellipsis );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      TextStyleKeys.TEXT_OVERFLOW_MODE,
      TextStyleKeys.TEXT_OVERFLOW_ELLIPSIS
    };
  }
}
