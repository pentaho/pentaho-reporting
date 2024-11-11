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

import org.pentaho.reporting.libraries.css.keys.text.TextOverflowMode;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 02.12.2005, 19:29:09
 *
 * @author Thomas Morgner
 */
public class TextOverflowModeReadHandler extends OneOfConstantsReadHandler {
  public TextOverflowModeReadHandler() {
    super( false );
    addValue( TextOverflowMode.CLIP );
    addValue( TextOverflowMode.ELLIPSIS );
    addValue( TextOverflowMode.ELLIPSIS_WORD );
  }
}
