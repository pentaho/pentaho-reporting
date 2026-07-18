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



package org.pentaho.reporting.libraries.css.resolver.values.computed.lists;

import org.pentaho.reporting.libraries.css.keys.list.ListStylePosition;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

public class ListStylePositionResolveHandler extends ConstantsResolveHandler {
  public ListStylePositionResolveHandler() {
    addNormalizeValue( ListStylePosition.INSIDE );
    addNormalizeValue( ListStylePosition.OUTSIDE );
    setFallback( ListStylePosition.OUTSIDE );
  }
}
