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
