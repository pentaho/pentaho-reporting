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


package org.pentaho.reporting.libraries.css.parser.stylehandler.hyperlinks;

import org.pentaho.reporting.libraries.css.keys.hyperlinks.TargetName;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 28.11.2005, 19:30:40
 *
 * @author Thomas Morgner
 */
public class TargetNameReadHandler extends OneOfConstantsReadHandler {
  public TargetNameReadHandler() {
    super( false );
    addValue( TargetName.CURRENT );
    addValue( TargetName.MODAL );
    addValue( TargetName.NEW );
    addValue( TargetName.PARENT );
    addValue( TargetName.ROOT );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ) {
      return new CSSStringValue( CSSStringType.STRING, value.getStringValue() );
    }
    return super.lookupValue( value );
  }
}
