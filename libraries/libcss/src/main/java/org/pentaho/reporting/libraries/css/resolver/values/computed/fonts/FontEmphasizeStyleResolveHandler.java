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

import org.pentaho.reporting.libraries.css.keys.font.FontEmphasizeStyle;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 18.12.2005, 16:32:08
 *
 * @author Thomas Morgner
 */
public class FontEmphasizeStyleResolveHandler extends ConstantsResolveHandler {
  public FontEmphasizeStyleResolveHandler() {
    addNormalizeValue( FontEmphasizeStyle.ACCENT );
    addNormalizeValue( FontEmphasizeStyle.CIRCLE );
    addNormalizeValue( FontEmphasizeStyle.DISC );
    addNormalizeValue( FontEmphasizeStyle.DOT );
    addNormalizeValue( FontEmphasizeStyle.NONE );
    setFallback( FontEmphasizeStyle.NONE );
  }
}
