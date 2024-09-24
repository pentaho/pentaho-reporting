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

package org.pentaho.reporting.libraries.css.parser.stylehandler;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 03.12.2005, 19:10:30
 *
 * @author Thomas Morgner
 */
public class AbstractCompoundValueReadHandler
  implements CSSCompoundValueReadHandler {
  private HashMap handlers;

  public AbstractCompoundValueReadHandler() {
    this.handlers = new HashMap();
  }

  protected synchronized void addHandler( StyleKey key, CSSValueReadHandler handler ) {
    this.handlers.put( key, handler );
  }

  public synchronized StyleKey[] getAffectedKeys() {
    return (StyleKey[])
      handlers.keySet().toArray( new StyleKey[ handlers.size() ] );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public synchronized Map createValues( LexicalUnit unit ) {
    final Map map = new HashMap();
    final Map.Entry[] entries = (Map.Entry[])
      handlers.entrySet().toArray( new Map.Entry[ handlers.size() ] );
    while ( unit != null ) {
      for ( int i = 0; i < entries.length; i++ ) {
        Map.Entry entry = entries[ i ];
        CSSValueReadHandler valueReadHandler = (CSSValueReadHandler) entry.getValue();
        StyleKey key = (StyleKey) entry.getKey();
        CSSValue value = valueReadHandler.createValue( key, unit );
        if ( value != null ) {
          map.put( key, value );
          break;
        }
      }
      unit = unit.getNextLexicalUnit();
    }
    return map;
  }
}
