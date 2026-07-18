/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.PunctuationTrim;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 02.12.2005, 19:42:01
 *
 * @author Thomas Morgner
 */
public class PunctuationTrimReadHandler extends OneOfConstantsReadHandler {
  public PunctuationTrimReadHandler() {
    super( false );
    addValue( PunctuationTrim.NONE );
    addValue( PunctuationTrim.START );
  }
}
