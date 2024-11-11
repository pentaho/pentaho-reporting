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


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class PaginationShiftStatePool {
  private static class BlockPool extends StackedObjectPool<BlockLevelPaginationShiftState> {
    private BlockPool() {
    }

    protected BlockLevelPaginationShiftState create() {
      return new BlockLevelPaginationShiftState();
    }
  }

  private static class RowPool extends StackedObjectPool<RowLevelPaginationShiftState> {
    private RowPool() {
    }

    protected RowLevelPaginationShiftState create() {
      return new RowLevelPaginationShiftState();
    }
  }

  private StackedObjectPool<BlockLevelPaginationShiftState> blockPool;
  private StackedObjectPool<RowLevelPaginationShiftState> rowPool;

  public PaginationShiftStatePool() {
    blockPool = new BlockPool();
    rowPool = new RowPool();
  }

  protected boolean isBlock( final int nodeType ) {
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      return true;
    }

    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_TABLE ) == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return true;
    }

    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      return true;
    }
    if ( ( nodeType & LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
      return true;
    }
    return false;
  }

  public PaginationShiftState create( RenderBox box, PaginationShiftState parent ) {
    final int nodeType = box.getLayoutNodeType();
    if ( isBlock( nodeType ) ) {
      BlockLevelPaginationShiftState blockShiftState = blockPool.get();
      blockShiftState.reuse( blockPool, parent, box );
      return blockShiftState;
    }

    RowLevelPaginationShiftState shiftState = rowPool.get();
    shiftState.reuse( rowPool, parent, box );
    return shiftState;
  }
}
