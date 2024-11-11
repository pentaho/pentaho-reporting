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

import org.pentaho.reporting.libraries.css.keys.font.FontEmphasizePosition;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 18.12.2005, 16:31:08
 *
 * @author Thomas Morgner
 */
public class FontEmphasizePositionResolveHandler extends ConstantsResolveHandler {
  public FontEmphasizePositionResolveHandler() {
    addNormalizeValue( FontEmphasizePosition.AFTER );
    addNormalizeValue( FontEmphasizePosition.BEFORE );
    addValue( FontEmphasizePosition.ABOVE, FontEmphasizePosition.BEFORE );
    addValue( FontEmphasizePosition.BELOW, FontEmphasizePosition.AFTER );
    setFallback( FontEmphasizePosition.BEFORE );
  }
}
