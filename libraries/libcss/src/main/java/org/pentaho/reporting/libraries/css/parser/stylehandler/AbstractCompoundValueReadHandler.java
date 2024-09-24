/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
