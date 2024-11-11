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


package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontFamilyValues;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.stylehandler.ListOfValuesReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 16:18:46
 *
 * @author Thomas Morgner
 */
public class FontFamilyReadHandler extends ListOfValuesReadHandler {
  public FontFamilyReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "none" ) ) {
        return FontFamilyValues.NONE;
      }
    }
    return super.createValue( name, value );
  }

  protected CSSValue parseValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "serif" ) ) {
        return FontFamilyValues.SERIF;
      }
      if ( value.getStringValue().equalsIgnoreCase( "sans-serif" ) ) {
        return FontFamilyValues.SANS_SERIF;
      }
      if ( value.getStringValue().equalsIgnoreCase( "fantasy" ) ) {
        return FontFamilyValues.FANTASY;
      }
      if ( value.getStringValue().equalsIgnoreCase( "cursive" ) ) {
        return FontFamilyValues.CURSIVE;
      }
      if ( value.getStringValue().equalsIgnoreCase( "monospace" ) ) {
        return FontFamilyValues.MONOSPACE;
      }
      return null;
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ) {
      return new CSSStringValue( CSSStringType.STRING, value.getStringValue() );
    }
    return null;
  }
}
