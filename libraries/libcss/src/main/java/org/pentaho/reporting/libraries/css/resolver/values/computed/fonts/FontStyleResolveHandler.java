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


package org.pentaho.reporting.libraries.css.resolver.values.computed.fonts;

import org.pentaho.reporting.libraries.css.keys.font.FontStyle;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 18.12.2005, 20:55:31
 *
 * @author Thomas Morgner
 */
public class FontStyleResolveHandler extends ConstantsResolveHandler {
  public FontStyleResolveHandler() {
    addNormalizeValue( FontStyle.ITALIC );
    addNormalizeValue( FontStyle.NORMAL );
    addNormalizeValue( FontStyle.OBLIQUE );
    setFallback( FontStyle.NORMAL );
  }
}
