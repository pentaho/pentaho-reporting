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
