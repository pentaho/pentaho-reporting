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

package org.pentaho.reporting.libraries.css.resolver.values.computed.fonts;

import org.pentaho.reporting.libraries.css.keys.font.FontSmooth;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 18.12.2005, 15:26:56
 *
 * @author Thomas Morgner
 */
public class FontSmoothResolveHandler extends ConstantsResolveHandler {
  public FontSmoothResolveHandler() {
    addNormalizeValue( FontSmooth.ALWAYS );
    addNormalizeValue( FontSmooth.NEVER );
    setFallback( FontSmooth.NEVER );
  }
}
