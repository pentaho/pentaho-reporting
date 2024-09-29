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


package org.pentaho.reporting.libraries.css.parser.stylehandler.border;

import org.pentaho.reporting.libraries.css.keys.border.BackgroundRepeat;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 27.11.2005, 18:36:29
 *
 * @author Thomas Morgner
 */
public class BackgroundRepeatReadHandler implements CSSValueReadHandler {
  public BackgroundRepeatReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    ArrayList values = new ArrayList();

    while ( value != null ) {
      if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
        return null;
      }

      final CSSConstant horizontal;
      final CSSConstant vertical;

      final String horizontalString = value.getStringValue();
      if ( horizontalString.equalsIgnoreCase( "repeat-x" ) ) {
        horizontal = BackgroundRepeat.REPEAT;
        vertical = BackgroundRepeat.NOREPEAT;
      } else if ( value.getStringValue().equalsIgnoreCase( "repeat-y" ) ) {
        horizontal = BackgroundRepeat.NOREPEAT;
        vertical = BackgroundRepeat.REPEAT;
      } else {
        horizontal = translateRepeat( horizontalString );
        if ( horizontal == null ) {
          return null;
        }

        value = value.getNextLexicalUnit();
        if ( value == null ) {
          vertical = horizontal;
        } else if ( value.getLexicalUnitType() != LexicalUnit.SAC_IDENT ) {
          return null;
        } else {
          vertical = translateRepeat( value.getStringValue() );
          if ( vertical == null ) {
            return null;
          }
        }
      }

      values.add( new CSSValuePair( horizontal, vertical ) );
      value = CSSValueFactory.parseComma( value );
    }

    return new CSSValueList( values );
  }

  private CSSConstant translateRepeat( final String value ) {
    if ( value.equalsIgnoreCase( "repeat" ) ) {
      return BackgroundRepeat.REPEAT;
    }
    if ( value.equalsIgnoreCase( "no-repeat" ) ) {
      return BackgroundRepeat.NOREPEAT;
    }
    if ( value.equalsIgnoreCase( "space" ) ) {
      return BackgroundRepeat.SPACE;
    }
    return null;
  }
}
