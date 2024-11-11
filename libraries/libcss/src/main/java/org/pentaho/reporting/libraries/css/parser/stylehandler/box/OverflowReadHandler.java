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
import org.pentaho.reporting.libraries.css.keys.box.Overflow;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 28.11.2005, 16:06:42
 *
 * @author Thomas Morgner
 */
public class OverflowReadHandler extends OneOfConstantsReadHandler
  implements CSSCompoundValueReadHandler {
  public OverflowReadHandler() {
    super( true );
    addValue( Overflow.HIDDEN );
    addValue( Overflow.VISIBLE );
    addValue( Overflow.SCROLL );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final CSSValue value = lookupValue( unit );
    if ( value == null ) {
      return null;
    }

    final Map map = new HashMap();
    map.put( BoxStyleKeys.OVERFLOW_X, value );
    map.put( BoxStyleKeys.OVERFLOW_Y, value );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      BoxStyleKeys.OVERFLOW_X,
      BoxStyleKeys.OVERFLOW_Y
    };
  }
}
