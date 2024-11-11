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

import org.pentaho.reporting.libraries.css.keys.text.TextJustifyTrim;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 08.12.2005, 16:58:15
 *
 * @author Thomas Morgner
 */
public class TextJustifyTrimReadHandler extends OneOfConstantsReadHandler {
  public TextJustifyTrimReadHandler() {
    super( false );
    addValue( TextJustifyTrim.NONE );
    addValue( TextJustifyTrim.PUNCTUATION );
    addValue( TextJustifyTrim.PUNCTUATION_AND_KANA );
  }
}
