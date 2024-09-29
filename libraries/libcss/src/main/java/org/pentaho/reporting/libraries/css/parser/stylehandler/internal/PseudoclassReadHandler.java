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


package org.pentaho.reporting.libraries.css.parser.stylehandler.internal;

import org.pentaho.reporting.libraries.css.parser.stylehandler.ListOfValuesReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 12.02.2006, 20:35:04
 *
 * @author Thomas Morgner
 */
public class PseudoclassReadHandler extends ListOfValuesReadHandler {
  public PseudoclassReadHandler() {
  }

  protected CSSValue parseValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ||
      value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      return new CSSStringValue( CSSStringType.STRING, value.getStringValue() );
    }
    return null;
  }
}
