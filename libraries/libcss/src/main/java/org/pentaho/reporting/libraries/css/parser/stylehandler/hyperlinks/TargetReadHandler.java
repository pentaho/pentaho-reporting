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
