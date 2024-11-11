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

import org.pentaho.reporting.libraries.css.keys.font.FontVariant;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 18.12.2005, 20:55:31
 *
 * @author Thomas Morgner
 */
public class FontVariantResolveHandler extends ConstantsResolveHandler {
  public FontVariantResolveHandler() {
    addNormalizeValue( FontVariant.SMALL_CAPS );
    addNormalizeValue( FontVariant.NORMAL );
    setFallback( FontVariant.NORMAL );
  }
}
