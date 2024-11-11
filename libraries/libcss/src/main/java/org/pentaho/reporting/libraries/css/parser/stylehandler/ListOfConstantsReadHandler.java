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


package org.pentaho.reporting.libraries.css.parser.stylehandler;

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
public abstract class ListOfConstantsReadHandler extends ListOfValuesReadHandler {
  private HashMap constants;
  private boolean autoAllowed;

  protected ListOfConstantsReadHandler( final boolean auto ) {
    this( Integer.MAX_VALUE, auto, false );
  }

  protected ListOfConstantsReadHandler( final int maxCount,
                                        final boolean auto,
                                        final boolean distinct ) {
    super( maxCount, distinct );
    constants = new HashMap();
    this.autoAllowed = auto;
    if ( autoAllowed ) {
      constants.put( "auto", CSSAutoValue.getInstance() );
    }
  }

  public void addValue( CSSConstant constant ) {
    constants.put( constant.getCSSText().toLowerCase(), constant );
  }

  protected CSSValue parseValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    return (CSSValue) constants.get( value.getStringValue().toLowerCase() );
  }

  public boolean isAutoAllowed() {
    return autoAllowed;
  }
}
