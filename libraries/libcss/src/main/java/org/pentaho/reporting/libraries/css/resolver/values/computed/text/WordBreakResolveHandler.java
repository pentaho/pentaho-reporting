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


package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.keys.text.WordBreak;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 15:12:04
 *
 * @author Thomas Morgner
 */
public class WordBreakResolveHandler extends ConstantsResolveHandler {
  public WordBreakResolveHandler() {
    addNormalizeValue( WordBreak.BREAK_ALL );
    addNormalizeValue( WordBreak.BREAK_STRICT );
    addNormalizeValue( WordBreak.KEEP_ALL );
    addNormalizeValue( WordBreak.LOOSE );
    addNormalizeValue( WordBreak.NORMAL );
    setFallback( WordBreak.NORMAL );
  }

}
