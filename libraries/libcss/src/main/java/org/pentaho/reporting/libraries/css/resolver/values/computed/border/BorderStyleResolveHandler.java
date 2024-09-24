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

package org.pentaho.reporting.libraries.css.resolver.values.computed.border;

import org.pentaho.reporting.libraries.css.keys.border.BorderStyle;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 14.12.2005, 23:27:55
 *
 * @author Thomas Morgner
 */
public class BorderStyleResolveHandler extends ConstantsResolveHandler {
  public BorderStyleResolveHandler() {
    addNormalizeValue( BorderStyle.DASHED );
    addNormalizeValue( BorderStyle.DOT_DASH );
    addNormalizeValue( BorderStyle.DOT_DOT_DASH );
    addNormalizeValue( BorderStyle.DOTTED );
    addNormalizeValue( BorderStyle.DOUBLE );
    addNormalizeValue( BorderStyle.GROOVE );
    addNormalizeValue( BorderStyle.HIDDEN );
    addNormalizeValue( BorderStyle.INSET );
    addNormalizeValue( BorderStyle.NONE );
    addNormalizeValue( BorderStyle.OUTSET );
    addNormalizeValue( BorderStyle.RIDGE );
    addNormalizeValue( BorderStyle.SOLID );
    addNormalizeValue( BorderStyle.WAVE );
    setFallback( BorderStyle.NONE );
  }


}
