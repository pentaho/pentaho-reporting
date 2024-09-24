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

package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.keys.text.TextOverflowMode;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;


/**
 * Creation-Date: 21.12.2005, 16:48:23
 *
 * @author Thomas Morgner
 */
public class TextOverflowModeResolveHandler extends ConstantsResolveHandler {
  public TextOverflowModeResolveHandler() {
    addNormalizeValue( TextOverflowMode.CLIP );
    addNormalizeValue( TextOverflowMode.ELLIPSIS );
    addNormalizeValue( TextOverflowMode.ELLIPSIS_WORD );
    setFallback( TextOverflowMode.CLIP );
  }

}
