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



package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

public class ItemBandReadHandler extends AbstractRootLevelBandReadHandler {
  public ItemBandReadHandler() throws ParseException {
    super( ItemBandType.INSTANCE );
  }

  public ItemBand getElement() {
    return (ItemBand) super.getElement();
  }
}
