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

import org.pentaho.reporting.libraries.css.keys.font.FontEffects;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 18.12.2005, 15:30:36
 *
 * @author Thomas Morgner
 */
public class FontEffectResolveHandler extends ConstantsResolveHandler {
  public FontEffectResolveHandler() {
    addNormalizeValue( FontEffects.EMBOSS );
    addNormalizeValue( FontEffects.ENGRAVE );
    addNormalizeValue( FontEffects.NONE );
    addNormalizeValue( FontEffects.OUTLINE );
    setFallback( FontEffects.NONE );
  }
}
