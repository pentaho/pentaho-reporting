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


package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Warning: This *is* a compound property, but one if its values depend on the element structure and it changes its
 * meaning if used in Tables.
 *
 * @author Thomas Morgner
 */
public class DropInitialValueReadHandler extends OneOfConstantsReadHandler {
  public DropInitialValueReadHandler() {
    super( false );
    addValue( new CSSConstant( "initial" ) );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    CSSValue constant = super.lookupValue( value );
    if ( constant != null ) {
      return constant;
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_INTEGER ) {
      return CSSNumericValue.createValue( CSSNumericType.NUMBER,
        value.getIntegerValue() );
    }

    return null;

  }
}
