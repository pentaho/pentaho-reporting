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

package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.TextHeight;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 17:58:02
 *
 * @author Thomas Morgner
 */
public class TextHeightReadHandler extends OneOfConstantsReadHandler {
  public TextHeightReadHandler() {
    super( true );
    addValue( TextHeight.FONT_SIZE );
    addValue( TextHeight.MAX_SIZE );
    addValue( TextHeight.TEXT_SIZE );
  }
}
