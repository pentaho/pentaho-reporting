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
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;

/**
 * Creation-Date: 26.11.2005, 19:16:43
 *
 * @author Thomas Morgner
 */
public abstract class OneOfConstantsReadHandler implements CSSValueReadHandler {
  private HashMap constants;
  private boolean autoAllowed;

  protected OneOfConstantsReadHandler( final boolean auto ) {
    constants = new HashMap();
    this.autoAllowed = auto;
    if ( autoAllowed ) {
      constants.put( "auto", CSSAutoValue.getInstance() );
    }
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    return lookupValue( value );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    return (CSSValue) constants.get( value.getStringValue().toLowerCase() );
  }

  protected void addValue( CSSConstant constant ) {
    constants.put( constant.getCSSText().toLowerCase(), constant );
  }

  public boolean isAutoAllowed() {
    return autoAllowed;
  }
}
