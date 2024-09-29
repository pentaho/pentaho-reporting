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


package org.pentaho.reporting.libraries.css.parser.stylehandler.page;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 08.12.2005, 17:29:41
 *
 * @author Thomas Morgner
 */
public class ImageOrientationReadHandler implements CSSValueReadHandler {
  public ImageOrientationReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      String ident = value.getStringValue();
      if ( ident.equalsIgnoreCase( "auto" ) ) {
        return CSSAutoValue.getInstance();
      }
      return null;
    }
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_DEGREE ) {
      return null;
    }
    return CSSNumericValue.createValue( CSSNumericType.DEG, value.getFloatValue() );
  }
}
