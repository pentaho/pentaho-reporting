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


package org.pentaho.reporting.libraries.css.parser.stylehandler.color;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.CSSValueReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 27.11.2005, 20:30:15
 *
 * @author Thomas Morgner
 */
public class OpacityReadHandler implements CSSValueReadHandler {
  public OpacityReadHandler() {
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    // normalization is deferred until we compute the value.
    return CSSValueFactory.createNumericValue( value );
  }
}
