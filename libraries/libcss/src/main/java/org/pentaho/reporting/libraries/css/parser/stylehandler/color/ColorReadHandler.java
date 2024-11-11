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


package org.pentaho.reporting.libraries.css.parser.stylehandler.color;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.util.ColorUtil;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 26.11.2005, 20:22:46
 *
 * @author Thomas Morgner
 */
public class ColorReadHandler implements CSSValueReadHandler {

  public ColorReadHandler() {
  }

  public CSSValue createValue( final StyleKey name, final LexicalUnit value ) {
    return createColorValue( value );
  }

  public static CSSValue createColorValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION ||
      value.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR ) {
      return CSSValueFactory.parseFunction( value );
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      // a constant color name
      return ColorUtil.parseIdentColor( value.getStringValue() );
    }
    return null;
  }

}
