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

package org.pentaho.reporting.libraries.css.parser.stylehandler.hyperlinks;

import org.pentaho.reporting.libraries.css.keys.hyperlinks.HyperlinkStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 28.11.2005, 19:34:19
 *
 * @author Thomas Morgner
 */
public class TargetReadHandler implements CSSCompoundValueReadHandler {
  private TargetNameReadHandler nameReadHandler;
  private TargetNewReadHandler newReadHandler;
  private TargetPositionReadHandler positionReadHandler;

  public TargetReadHandler() {
    nameReadHandler = new TargetNameReadHandler();
    newReadHandler = new TargetNewReadHandler();
    positionReadHandler = new TargetPositionReadHandler();
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    CSSValue nameValue = nameReadHandler.createValue( null, unit );
    if ( nameValue != null ) {
      unit = unit.getNextLexicalUnit();
    }

    CSSValue newValue = null;
    if ( unit != null ) {
      newValue = newReadHandler.createValue( null, unit );
      if ( newValue != null ) {
        unit = unit.getNextLexicalUnit();
      }
    }
    CSSValue positionValue = null;
    if ( unit != null ) {
      positionValue = positionReadHandler.createValue( null, unit );
    }

    Map map = new HashMap();
    if ( nameValue != null ) {
      map.put( HyperlinkStyleKeys.TARGET_NAME, nameValue );
    }
    if ( newValue != null ) {
      map.put( HyperlinkStyleKeys.TARGET_NEW, newValue );
    }
    if ( positionValue != null ) {
      map.put( HyperlinkStyleKeys.TARGET_POSITION, positionValue );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      HyperlinkStyleKeys.TARGET_NAME,
      HyperlinkStyleKeys.TARGET_NEW,
      HyperlinkStyleKeys.TARGET_POSITION
    };
  }
}
