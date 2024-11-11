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


package org.pentaho.reporting.libraries.css.parser.stylehandler.positioning;

import org.pentaho.reporting.libraries.css.keys.positioning.Position;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 21.12.2005, 18:32:53
 *
 * @author Thomas Morgner
 */
public class PositionReadHandler extends OneOfConstantsReadHandler {
  public PositionReadHandler() {
    super( false );
    addValue( Position.ABSOLUTE );
    addValue( Position.FIXED );
    addValue( Position.RELATIVE );
    addValue( Position.STATIC );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    final CSSValue result = super.createValue( name, value );
    if ( result != null ) {
      return result;
    }

    // maybe the position is a 'running(..)' function.
    if ( CSSValueFactory.isFunctionValue( value ) ) {
      final CSSFunctionValue cssFunctionValue = CSSValueFactory.parseFunction( value );
      if ( cssFunctionValue != null ) {
        // we are a bit restrictive for now ..
        if ( "running".equals( cssFunctionValue.getFunctionName() ) ) {
          return cssFunctionValue;
        }
      }
    }
    return null;
  }
}
