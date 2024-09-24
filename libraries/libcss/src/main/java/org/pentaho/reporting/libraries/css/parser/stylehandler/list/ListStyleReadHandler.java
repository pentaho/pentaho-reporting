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

package org.pentaho.reporting.libraries.css.parser.stylehandler.list;

import org.pentaho.reporting.libraries.css.keys.list.ListStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 01.12.2005, 19:36:10
 *
 * @author Thomas Morgner
 */
public class ListStyleReadHandler implements CSSCompoundValueReadHandler {
  private ListStyleImageReadHandler imageReadHandler;
  private ListStylePositionReadHandler positionReadHandler;
  private ListStyleTypeReadHandler typeReadHandler;

  public ListStyleReadHandler() {
    imageReadHandler = new ListStyleImageReadHandler();
    positionReadHandler = new ListStylePositionReadHandler();
    typeReadHandler = new ListStyleTypeReadHandler();
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    CSSValue type = typeReadHandler.createValue( null, unit );
    if ( type != null ) {
      unit = unit.getNextLexicalUnit();
    }
    CSSValue position = null;
    if ( unit != null ) {
      position = positionReadHandler.createValue( null, unit );
      if ( position != null ) {
        unit = unit.getNextLexicalUnit();
      }
    }
    CSSValue image = null;
    if ( unit != null ) {
      image = imageReadHandler.createValue( null, unit );
    }

    Map map = new HashMap();
    if ( type != null ) {
      map.put( ListStyleKeys.LIST_STYLE_TYPE, type );
    }
    if ( position != null ) {
      map.put( ListStyleKeys.LIST_STYLE_POSITION, position );
    }
    if ( image != null ) {
      map.put( ListStyleKeys.LIST_STYLE_IMAGE, image );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      ListStyleKeys.LIST_STYLE_IMAGE,
      ListStyleKeys.LIST_STYLE_POSITION,
      ListStyleKeys.LIST_STYLE_TYPE
    };
  }
}
