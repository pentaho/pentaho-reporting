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

import org.pentaho.reporting.libraries.css.keys.text.WordWrap;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 19:45:53
 *
 * @author Thomas Morgner
 */
public class WordWrapReadHandler extends OneOfConstantsReadHandler {
  public WordWrapReadHandler() {
    super( false );
    addValue( WordWrap.BREAK_WORD );
    addValue( WordWrap.NORMAL );
  }
}
