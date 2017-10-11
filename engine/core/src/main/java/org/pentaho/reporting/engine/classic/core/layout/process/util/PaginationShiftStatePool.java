/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
