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

package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontSizeConstant;
import org.pentaho.reporting.libraries.css.keys.font.RelativeFontSize;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 16:51:33
 *
 * @author Thomas Morgner
 */
public class FontSizeReadHandler extends OneOfConstantsReadHandler {
  public FontSizeReadHandler() {
    this( false );
  }

  protected FontSizeReadHandler( final boolean autoAllowed ) {
    super( autoAllowed );
    addValue( FontSizeConstant.LARGE );
    addValue( FontSizeConstant.MEDIUM );
    addValue( FontSizeConstant.SMALL );
    addValue( FontSizeConstant.X_LARGE );
    addValue( FontSizeConstant.XX_LARGE );
    addValue( FontSizeConstant.X_SMALL );
    addValue( FontSizeConstant.XX_SMALL );
    addValue( RelativeFontSize.LARGER );
    addValue( RelativeFontSize.SMALLER );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE, value.getFloatValue() );
    }
    CSSValue constant = super.lookupValue( value );
    if ( constant != null ) {
      return constant;
    }
    return CSSValueFactory.createLengthValue( value );
  }
}
