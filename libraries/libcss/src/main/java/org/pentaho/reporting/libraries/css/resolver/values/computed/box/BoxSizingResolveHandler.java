/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.resolver.values.computed.box;

import org.pentaho.reporting.libraries.css.keys.box.BoxSizing;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class BoxSizingResolveHandler extends ConstantsResolveHandler {
  public BoxSizingResolveHandler() {
    addNormalizeValue( BoxSizing.BORDER_BOX );
    addNormalizeValue( BoxSizing.CONTENT_BOX );
    setFallback( BoxSizing.CONTENT_BOX );
  }

}
