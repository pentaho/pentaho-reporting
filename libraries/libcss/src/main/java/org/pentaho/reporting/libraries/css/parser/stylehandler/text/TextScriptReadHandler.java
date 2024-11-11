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


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 08.12.2005, 17:10:11
 *
 * @author Thomas Morgner
 */
public class TextScriptReadHandler implements CSSValueReadHandler {
  public TextScriptReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    if ( value.getStringValue().equalsIgnoreCase( "auto" ) ) {
      return CSSAutoValue.getInstance();
    }
    return new CSSConstant( value.getStringValue() );
  }
}
