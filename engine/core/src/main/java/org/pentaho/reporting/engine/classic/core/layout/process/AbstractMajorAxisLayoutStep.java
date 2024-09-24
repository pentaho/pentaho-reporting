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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public abstract class AbstractMajorAxisLayoutStep extends IterateVisualProcessStep {
  // Set the maximum height to an incredibly high value. This is now 2^43 micropoints or more than
  // 3000 kilometers. Please call me directly at any time if you need more space for printing.
  protected static final long MAX_AUTO = StrictGeomUtility.MAX_AUTO;

  private boolean cacheClean;
  private TableRowHeightCalculation tableRowHeightStep;
  private InstanceID allChildsDirtyMarker;

  protected AbstractMajorAxisLayoutStep( final boolean secondPass ) {
    this.tableRowHeightStep = new TableRowHeightCalculation( secondPass );
  }

  protected TableRowHeightCalculation getTableRowHeightStep() {
    return tableRowHeightStep;
  }

  public void compute( final LogicalPageBox pageBox ) {
    getEventWatch().start();
    getSummaryWatch().start();
    try {
      this.tableRowHeightStep.reset();
      this.cacheClean = true;
      startProcessing( pageBox );
    } finally {
      getSummaryWatch().stop( true );
      getEventWatch().stop();
    }
  }

  public void continueComputation( final RenderBox pageBox ) {
    this.tableRowHeightStep.reset();
    this.cacheClean = true;
    startProcessing( pageBox );
  }

  protected void markAllChildsDirty( final RenderNode node ) {
    InstanceID instanceId = node.getInstanceId();
    if ( instanceId == null ) {
      return;
    }
    if ( this.allChildsDirtyMarker != null ) {
      return;
    }
    this.allChildsDirtyMarker = instanceId;
  }

  public void clearAllChildsDirtyMarker( final RenderNode node ) {
    InstanceID instanceId = node.getInstanceId();
    if ( instanceId == null ) {
      return;
    }
    if ( this.allChildsDirtyMarker == instanceId ) {
      this.allChildsDirtyMarker = null;
    }
  }

  protected boolean checkCacheValid( final RenderNode node ) {
    if ( cacheClean == false ) {
      return false;
    }

    if ( allChildsDirtyMarker != null ) {
      return false;
    }

    final RenderNode.CacheState cacheState = node.getCacheState();
    if ( cacheState == RenderNode.CacheState.DEEP_DIRTY ) {
      cacheClean = false;
    }

    if ( cacheClean && node.isCacheValid() ) {
      return true;
    }
    return false;
  }

  protected void performStartTable( final RenderBox box ) {
    final int nodeType = box.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      tableRowHeightStep.startTableBox( (TableRenderBox) box );
    }
  }

  protected void performFinishTable( final RenderBox box ) {
    final int nodeType = box.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      tableRowHeightStep.finishTable( (TableRenderBox) box );
    }
  }

  protected boolean isCacheClean() {
    return cacheClean;
  }
}
