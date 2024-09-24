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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.LineGridProgression;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

/**
 * Creation-Date: 03.12.2005, 19:50:46
 *
 * @author Thomas Morgner
 */
public class LineGridProgressionReadHandler extends OneOfConstantsReadHandler {
  public LineGridProgressionReadHandler() {
    super( false );
    addValue( LineGridProgression.LINE_HEIGHT );
    addValue( LineGridProgression.TEXT_HEIGHT );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    CSSValue length = CSSValueFactory.createLengthValue( value );
    if ( length != null ) {
      return length;
    }
    return super.lookupValue( value );
  }
}
