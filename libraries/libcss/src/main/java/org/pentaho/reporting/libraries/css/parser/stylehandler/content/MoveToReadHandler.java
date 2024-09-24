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

package org.pentaho.reporting.libraries.css.parser.stylehandler.content;

import org.pentaho.reporting.libraries.css.keys.content.MoveToValues;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 01.12.2005, 17:53:49
 *
 * @author Thomas Morgner
 */
public class MoveToReadHandler extends OneOfConstantsReadHandler {
  public MoveToReadHandler() {
    super( false );
    addValue( MoveToValues.HERE );
    addValue( MoveToValues.NORMAL );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    CSSValue content = super.lookupValue( value );
    if ( content != null ) {
      return content;
    }

    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    return new CSSConstant( value.getStringValue() );
  }
}
