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
