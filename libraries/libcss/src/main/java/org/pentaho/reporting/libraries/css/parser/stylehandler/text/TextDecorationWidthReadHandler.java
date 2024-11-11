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

import org.pentaho.reporting.libraries.css.keys.text.TextDecorationWidth;
import org.pentaho.reporting.libraries.css.parser.stylehandler.border.BorderWidthReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 02.12.2005, 20:10:24
 *
 * @author Thomas Morgner
 */
public class TextDecorationWidthReadHandler extends BorderWidthReadHandler {
  public TextDecorationWidthReadHandler() {
    super( true, true );
  }

  protected CSSValue parseWidth( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "dash" ) ) {
        return TextDecorationWidth.DASH;
      }
      if ( value.getStringValue().equalsIgnoreCase( "bold" ) ) {
        return TextDecorationWidth.BOLD;
      }
    }
    return super.parseWidth( value );
  }

}
