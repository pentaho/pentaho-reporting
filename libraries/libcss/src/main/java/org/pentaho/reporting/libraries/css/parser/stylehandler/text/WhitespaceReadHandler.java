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


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.keys.text.TextWrap;
import org.pentaho.reporting.libraries.css.keys.text.WhitespaceCollapse;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSInheritValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 01.12.2005, 22:18:58
 *
 * @author Thomas Morgner
 */
public class WhitespaceReadHandler implements CSSCompoundValueReadHandler {
  public WhitespaceReadHandler() {
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    // http://cheeaun.phoenity.com/weblog/2005/06/whitespace-and-generated-content.html
    // is a good overview about the whitespace stuff ..

    CSSValue whitespace;
    CSSValue textWrap;
    if ( unit.getLexicalUnitType() == LexicalUnit.SAC_INHERIT ) {
      whitespace = CSSInheritValue.getInstance();
      textWrap = CSSInheritValue.getInstance();
    } else if ( unit.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      String strVal = unit.getStringValue();
      if ( strVal.equalsIgnoreCase( "normal" ) ) {
        whitespace = WhitespaceCollapse.COLLAPSE;
        textWrap = TextWrap.NORMAL;
      } else if ( strVal.equalsIgnoreCase( "pre" ) ) {
        whitespace = WhitespaceCollapse.PRESERVE;
        textWrap = TextWrap.SUPPRESS;
      } else if ( strVal.equalsIgnoreCase( "pre-line" ) ) {
        whitespace = WhitespaceCollapse.PRESERVE_BREAKS;
        textWrap = TextWrap.NORMAL;
      } else if ( strVal.equalsIgnoreCase( "pre-wrap" ) ) {
        whitespace = WhitespaceCollapse.PRESERVE;
        textWrap = TextWrap.NORMAL;
      } else {
        return null;
      }
    } else {
      return null;
    }

    Map map = new HashMap();
    map.put( TextStyleKeys.WHITE_SPACE_COLLAPSE, whitespace );
    map.put( TextStyleKeys.TEXT_WRAP, textWrap );
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      TextStyleKeys.WHITE_SPACE_COLLAPSE,
      TextStyleKeys.TEXT_WRAP
    };
  }
}
