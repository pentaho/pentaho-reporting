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

package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontStyle;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 16:41:42
 *
 * @author Thomas Morgner
 */
public class FontStyleReadHandler extends OneOfConstantsReadHandler {
  public FontStyleReadHandler() {
    super( false );
    addValue( FontStyle.ITALIC );
    addValue( FontStyle.NORMAL );
    addValue( FontStyle.OBLIQUE );
  }
}
