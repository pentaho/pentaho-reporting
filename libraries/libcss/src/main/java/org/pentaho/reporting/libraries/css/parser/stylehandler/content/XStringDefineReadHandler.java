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

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 30.05.2006, 14:56:09
 *
 * @author Thomas Morgner
 */
public class XStringDefineReadHandler implements CSSValueReadHandler {
  public XStringDefineReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
      return null;
    }
    final String mayBeNone = value.getStringValue();
    if ( "none".equalsIgnoreCase( mayBeNone ) ) {
      return new CSSConstant( "none" );
    }

    final ArrayList counterSpecs = new ArrayList();
    while ( value != null ) {
      if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
        return null;
      }
      final String identifier = value.getStringValue();
      value = value.getNextLexicalUnit();
      counterSpecs.add( new CSSConstant( identifier ) );
    }

    return new CSSValueList( counterSpecs );

  }
}
