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



package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class CrosstabCellType extends AbstractSectionType {
  public static final CrosstabCellType INSTANCE = new CrosstabCellType();

  public CrosstabCellType() {
    super( "crosstab-cell", true );
  }

  public ReportElement create() {
    return new CrosstabCell();
  }
}
