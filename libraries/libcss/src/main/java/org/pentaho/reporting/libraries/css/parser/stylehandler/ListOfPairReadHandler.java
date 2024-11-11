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

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 26.11.2005, 19:23:09
 *
 * @author Thomas Morgner
 */
public abstract class ListOfPairReadHandler implements CSSValueReadHandler {
  protected ListOfPairReadHandler() {
  }


  public synchronized CSSValue createValue( StyleKey name, LexicalUnit value ) {
    ArrayList values = new ArrayList();

    while ( value != null ) {
      final CSSValue firstPosition = parseFirstPosition( value );
      if ( firstPosition == null ) {
        return null;
      }

      value = value.getNextLexicalUnit();
      final CSSValue secondPosition = parseSecondPosition( value, firstPosition );
      if ( secondPosition == null ) {
        return null;
      }

      addToResultList( values, firstPosition, secondPosition );
      value = CSSValueFactory.parseComma( value );
    }

    return new CSSValueList( values );
  }

  protected void addToResultList( ArrayList values,
                                  CSSValue firstPosition,
                                  CSSValue secondPosition ) {
    values.add( new CSSValuePair( firstPosition, secondPosition ) );
  }

  protected abstract CSSValue parseFirstPosition( final LexicalUnit value );

  protected abstract CSSValue parseSecondPosition( final LexicalUnit value,
                                                   final CSSValue first );

}
