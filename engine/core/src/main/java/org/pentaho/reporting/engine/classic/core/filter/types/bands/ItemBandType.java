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


package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class ItemBandType extends AbstractSectionType {
  public static final ElementType INSTANCE = new ItemBandType();

  public ItemBandType() {
    super( "itemband", false );
  }

  public ReportElement create() {
    return new ItemBand();
  }

}
