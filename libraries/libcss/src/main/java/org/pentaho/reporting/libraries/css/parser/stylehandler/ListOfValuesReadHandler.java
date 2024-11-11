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
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 26.11.2005, 19:16:43
 *
 * @author Thomas Morgner
 */
public abstract class ListOfValuesReadHandler implements CSSValueReadHandler {
  private int maxCount;
  private boolean distinctValues;

  protected ListOfValuesReadHandler() {
    maxCount = Integer.MAX_VALUE;
    distinctValues = false;
  }

  protected ListOfValuesReadHandler( int maxCount, final boolean distinct ) {
    this.maxCount = maxCount;
    this.distinctValues = distinct;
  }

  public boolean isDistinctValues() {
    return distinctValues;
  }

  public int getMaxCount() {
    return maxCount;
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    final ArrayList list = new ArrayList();
    int count = 0;
    while ( value != null && count < maxCount ) {
      final CSSValue pvalue = parseValue( value );
      if ( pvalue == null ) {
        return null;
      }
      if ( distinctValues == false ||
        list.contains( pvalue ) == false ) {
        list.add( pvalue );
      }
      value = CSSValueFactory.parseComma( value );
      count += 1;
    }

    return new CSSValueList( list );
  }

  protected abstract CSSValue parseValue( final LexicalUnit value );
}
