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
import org.pentaho.reporting.libraries.css.parser.stylehandler.color.ColorReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 08.12.2005, 17:03:03
 *
 * @author Thomas Morgner
 */
public class TextLineColorReadHandler extends ColorReadHandler {
  public TextLineColorReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "auto" ) ) {
        return CSSAutoValue.getInstance();
      }
    }
    return super.createValue( name, value );
  }
}
