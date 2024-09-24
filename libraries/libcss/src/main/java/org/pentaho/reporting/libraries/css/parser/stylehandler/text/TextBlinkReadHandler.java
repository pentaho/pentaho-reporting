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

import org.pentaho.reporting.libraries.css.keys.text.TextBlink;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 08.12.2005, 16:52:39
 *
 * @author Thomas Morgner
 */
public class TextBlinkReadHandler extends OneOfConstantsReadHandler {
  public TextBlinkReadHandler() {
    super( false );
    addValue( TextBlink.BLINK );
    addValue( TextBlink.NONE );
  }
}
