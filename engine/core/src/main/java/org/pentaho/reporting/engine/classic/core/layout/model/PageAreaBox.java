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



package org.pentaho.reporting.engine.classic.core.layout.model;

public class PageAreaBox extends BlockRenderBox {
  private LogicalPageBox logicalPage;

  public PageAreaBox() {
  }

  public void setLogicalPage( final LogicalPageBox logicalPage ) {
    this.logicalPage = logicalPage;
  }

  public LogicalPageBox getLogicalPage() {
    return logicalPage;
  }

  protected void updateChangeTracker() {
    super.updateChangeTracker();
    if ( logicalPage != null ) {
      logicalPage.updateChangeTracker();
    }

    if ( isParanoidModelChecks() && getCacheState() == RenderNode.CACHE_CLEAN ) {
      throw new IllegalStateException();
    }
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_PAGEAREA;
  }
}
