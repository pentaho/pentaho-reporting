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

import org.pentaho.reporting.libraries.css.keys.text.WhitespaceCollapse;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 15:12:04
 *
 * @author Thomas Morgner
 */
public class WhitespaceCollapseResolveHandler extends ConstantsResolveHandler {
  public WhitespaceCollapseResolveHandler() {
    addNormalizeValue( WhitespaceCollapse.COLLAPSE );
    addNormalizeValue( WhitespaceCollapse.DISCARD );
    addNormalizeValue( WhitespaceCollapse.PRESERVE );
    addNormalizeValue( WhitespaceCollapse.PRESERVE_BREAKS );
    setFallback( WhitespaceCollapse.COLLAPSE );
  }

}
