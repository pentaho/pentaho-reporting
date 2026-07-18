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



package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;

/**
 * A report band that displays a row of data items.
 *
 * @author David Gilbert
 */
public class ItemBand extends AbstractRootLevelBand {
  /**
   * Constructs an item band, containing no elements.
   */
  public ItemBand() {
    setElementType( new ItemBandType() );
  }

}
