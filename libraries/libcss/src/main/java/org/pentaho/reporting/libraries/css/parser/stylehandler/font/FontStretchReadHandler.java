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

import org.pentaho.reporting.libraries.css.keys.font.FontStretch;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 16:51:33
 *
 * @author Thomas Morgner
 */
public class FontStretchReadHandler extends OneOfConstantsReadHandler {
  public FontStretchReadHandler() {
    super( false );
    addValue( FontStretch.CONDENSED );
    addValue( FontStretch.EXPANDED );
    addValue( FontStretch.EXTRA_CONDENSED );
    addValue( FontStretch.EXTRA_EXPANDED );
    addValue( FontStretch.NORMAL );
    addValue( FontStretch.SEMI_CONDENSED );
    addValue( FontStretch.SEMI_EXPANDED );
    addValue( FontStretch.ULTRA_CONDENSED );
    addValue( FontStretch.ULTRA_EXPANDED );

    addValue( FontStretch.WIDER );
    addValue( FontStretch.NARROWER );
  }
}
