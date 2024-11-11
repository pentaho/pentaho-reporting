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

import org.pentaho.reporting.libraries.css.keys.text.TextJustify;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 19:47:58
 *
 * @author Thomas Morgner
 */
public class TextJustifyReadHandler extends OneOfConstantsReadHandler {
  public TextJustifyReadHandler() {
    super( true );
    addValue( TextJustify.INTER_CHARACTER );
    addValue( TextJustify.INTER_CLUSTER );
    addValue( TextJustify.INTER_IDEOGRAPH );
    addValue( TextJustify.INTER_WORD );
    addValue( TextJustify.KASHIDA );
    addValue( TextJustify.SIZE );
  }
}
