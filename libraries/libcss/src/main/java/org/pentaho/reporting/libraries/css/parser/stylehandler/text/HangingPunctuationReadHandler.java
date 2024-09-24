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

import org.pentaho.reporting.libraries.css.keys.text.HangingPunctuation;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 03.12.2005, 20:02:56
 *
 * @author Thomas Morgner
 */
public class HangingPunctuationReadHandler extends OneOfConstantsReadHandler {
  public HangingPunctuationReadHandler() {
    super( false );
    addValue( HangingPunctuation.BOTH );
    addValue( HangingPunctuation.END );
    addValue( HangingPunctuation.NONE );
    addValue( HangingPunctuation.START );
  }
}
