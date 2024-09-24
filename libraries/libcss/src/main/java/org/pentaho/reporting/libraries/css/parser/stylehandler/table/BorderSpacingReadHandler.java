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

package org.pentaho.reporting.libraries.css.parser.stylehandler.table;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 18.07.2006, 19:00:10
 *
 * @author Thomas Morgner
 */
public class BorderSpacingReadHandler implements CSSValueReadHandler {
  public BorderSpacingReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    CSSNumericValue firstValue = CSSValueFactory.createLengthValue( value );
    if ( firstValue == null ) {
      return null;
    }
    value = value.getNextLexicalUnit();
    CSSNumericValue secondValue;
    if ( value == null ) {
      secondValue = firstValue;
    } else {
      secondValue = CSSValueFactory.createLengthValue( value );
      if ( secondValue == null ) {
        return null;
      }
    }

    return new CSSValuePair( firstValue, secondValue );
  }

}
