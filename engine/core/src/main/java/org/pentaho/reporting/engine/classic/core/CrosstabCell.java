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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellType;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

public class CrosstabCell extends AbstractRootLevelBand {
  public CrosstabCell() {
    setElementType( CrosstabCellType.INSTANCE );
  }

  public String getColumnField() {
    return (String) getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.COLUMN_FIELD );
  }

  public void setColumnField( final String columnField ) {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.COLUMN_FIELD, columnField );
  }

  public String getRowField() {
    return (String) getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.ROW_FIELD );
  }

  public void setRowField( final String rowField ) {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.ROW_FIELD, rowField );
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return BandDefaultStyleSheet.getBandDefaultStyle();
  }
}
