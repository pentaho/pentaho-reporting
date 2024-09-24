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

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * Removed finished block-boxes. The boxes have to be marked as 'finished' by the flow output target or nothing will be
 * removed at all. The boxes marked as finished will be replaced by 'FinishedRenderNodes'. This step preserves nodes
 * that have pagebreaks.
 *
 * @author Thomas Morgner
 */
public final class CleanFlowBoxesStep extends CleanPaginatedBoxesStep {
  public CleanFlowBoxesStep() {
  }

  protected boolean checkFinishedForNode( final RenderNode currentNode ) {
    // todo: We should be able to consolidate the FINISH_TABLE and FINISH_PAGE flags into one.
    return currentNode.isFinishedTable();
  }

  public long compute( final LogicalPageBox pageBox ) {
    return super.compute( pageBox, pageBox.getProcessedTableOffset() );
  }
}
