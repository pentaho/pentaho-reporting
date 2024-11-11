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


package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;

public class TableLayoutInfo {
  private RenderLength rowSpacing;

  private boolean displayEmptyCells;
  private boolean collapsingBorderModel;
  private boolean autoLayout;

  public TableLayoutInfo() {
  }

  public RenderLength getRowSpacing() {
    return rowSpacing;
  }

  public void setRowSpacing( final RenderLength rowSpacing ) {
    this.rowSpacing = rowSpacing;
  }

  public boolean isDisplayEmptyCells() {
    return displayEmptyCells;
  }

  public void setDisplayEmptyCells( final boolean displayEmptyCells ) {
    this.displayEmptyCells = displayEmptyCells;
  }

  public boolean isCollapsingBorderModel() {
    return collapsingBorderModel;
  }

  public void setCollapsingBorderModel( final boolean collapsingBorderModel ) {
    this.collapsingBorderModel = collapsingBorderModel;
  }

  public boolean isAutoLayout() {
    return autoLayout;
  }

  public void setAutoLayout( final boolean autoLayout ) {
    this.autoLayout = autoLayout;
  }
}
