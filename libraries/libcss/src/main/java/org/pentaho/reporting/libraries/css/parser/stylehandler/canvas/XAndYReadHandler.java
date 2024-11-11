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


package org.pentaho.reporting.libraries.css.parser.stylehandler.canvas;

import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractWidthReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 27.11.2005, 21:33:25
 *
 * @author Thomas Morgner
 */
public class XAndYReadHandler extends AbstractWidthReadHandler {
  public XAndYReadHandler() {
    super( true, true );
  }

  protected CSSValue parseWidth( final LexicalUnit value ) {
    if ( CSSValueFactory.isNumericValue( value ) ) {
      return CSSValueFactory.createNumericValue( value );
    } else {
      return super.parseWidth( value );
    }
  }
}
