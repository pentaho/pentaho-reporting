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


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextAlign;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 19:47:58
 *
 * @author Thomas Morgner
 */
public class TextAlignLastReadHandler extends OneOfConstantsReadHandler {
  public TextAlignLastReadHandler() {
    super( true );
    addValue( TextAlign.CENTER );
    addValue( TextAlign.END );
    addValue( TextAlign.JUSTIFY );
    addValue( TextAlign.LEFT );
    addValue( TextAlign.RIGHT );
    addValue( TextAlign.START );
  }
}
