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


package org.pentaho.reporting.libraries.css.parser.stylehandler;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 27.11.2005, 21:16:17
 *
 * @author Thomas Morgner
 */
public abstract class AbstractWidthReadHandler implements CSSValueReadHandler {
  private boolean allowPercentages;
  private boolean allowAuto;

  protected AbstractWidthReadHandler( boolean allowPercentages,
                                      boolean allowAuto ) {
    this.allowPercentages = allowPercentages;
    this.allowAuto = allowAuto;
  }

  public boolean isAllowPercentages() {
    return allowPercentages;
  }

  public boolean isAllowAuto() {
    return allowAuto;
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    return parseWidth( value );
  }

  protected CSSValue parseWidth( final LexicalUnit value ) {
    if ( allowPercentages &&
      value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE,
        value.getFloatValue() );
    } else if ( allowAuto &&
      value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "auto" ) ) {
        return CSSAutoValue.getInstance();
      }
      return null;
    } else {
      return CSSValueFactory.createLengthValue( value );
    }
  }
}
