/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * This step must not remove boxes that have a manual break attached.
 *
 * @author Thomas Morgner
 */
public class CleanPaginatedBoxesStep extends IterateStructuralProcessStep {
  private static class TableSectionContext {
    private TableSectionContext context;
    private int safeRows;
    private int expectedNextRowNumber;

    private TableSectionContext( final TableSectionContext context ) {
      this.context = context;
    }

    public TableSectionContext pop() {
      return context;
    }

    public boolean isProcessingUnsafe() {
      // Are there any rows that could be removed? If not, take a short cut, and skip the body ..
      return safeRows <= expectedNextRowNumber;
    }
  }

  private long pageOffset;
  private long shiftOffset;
  private InstanceID shiftNode;
  private TableSectionContext tableSectionContext;

  public CleanPaginatedBoxesStep() {
  }

  public long getPageOffset() {
    return pageOffset;
  }

  public void setPageOffset( final long pageOffset ) {
    this.pageOffset = pageOffset;
  }

  protected long compute( final LogicalPageBox pageBox, final long pageOffset ) {
    this.shiftOffset = 0;
    this.pageOffset = pageOffset;
    if ( startBlockBox( pageBox ) ) {
      // not processing the header and footer area: they are 'out-of-context' bands
      processBoxChilds( pageBox );
    }
    finishBlockBox( pageBox );
    // Log.debug ("ShiftOffset after clean: " + shiftOffset);
    return shiftOffset;
  }

  public long compute( final LogicalPageBox pageBox ) {
    return compute( pageBox, pageBox.getPageOffset() );
  }

  public InstanceID getShiftNode() {
    return shiftNode;
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    // we do not process the paragraph lines. This should have been done
    // in the startblock thing and they get re-added anyway as long as the
    // paragraph is active.
  }

  public boolean startCanvasBox( final CanvasRenderBox box ) {
    return false;
  }

  protected boolean startRowBox( final RenderBox box ) {
    return true;
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return startBlockStyleBox( box );
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    // never remove table-column declarations. They are lightweight anyway ..
    return false;
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    tableSectionContext.expectedNextRowNumber = box.getRowIndex() + 1;
    // we dont remove cells from table-rows. If a table-row is finished, we may be able to remove the whole
    // row. However, we can remove contents from table-cells if needed.
    return true;
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    return true;
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    return startBlockBox( box );
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    tableSectionContext = new TableSectionContext( tableSectionContext );

    if ( box.getDisplayRole() == TableSectionRenderBox.Role.BODY ) {
      CleanTableRowsPreparationStep preparationStep = new CleanTableRowsPreparationStep();
      tableSectionContext.safeRows = preparationStep.process( box, pageOffset );
      tableSectionContext.expectedNextRowNumber = preparationStep.getFirstRowEncountered();

      if ( tableSectionContext.isProcessingUnsafe() ) {
        return false;
      }
      return startTableSectionStyleBox( box );
    }

    return false;
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    tableSectionContext = tableSectionContext.pop();
  }

  private boolean startBlockStyleBox( final RenderBox box ) {
    final int nodeType = box.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      return false;
    }

    boolean boxOutsideVisibleRange = ( box.getY() + box.getOverflowAreaHeight() ) <= pageOffset;
    final boolean safeForRemove = ( box.getParentWidowContexts() == 0 ) && boxOutsideVisibleRange;
    if ( safeForRemove || box.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED ) {
      // Next, search the last node that is fully invisible. We collapse all
      // invisible node into one big box for efficiency reasons. They wont be
      // visible anyway and thus the result will be the same as if they were
      // still alive ..
      final RenderNode firstNode = box.getFirstChild();
      RenderNode currentNode = firstNode;
      RenderNode lastToRemove = null;
      int orphanLeafCount = 0;
      int widowLeafCount = 0;

      while ( currentNode != null && currentNode.isOpen() == false && checkFinishedForNode( currentNode ) ) {
        if ( ( currentNode.getY() + currentNode.getOverflowAreaHeight() ) > pageOffset ) {
          // we cant handle that. This node will be visible. So the current last
          // node is the one we can shrink ..
          break;
        }

        orphanLeafCount = currentNode.getOrphanLeafCount();
        widowLeafCount = currentNode.getWidowLeafCount();
        lastToRemove = currentNode;
        currentNode = currentNode.getNext();
      }

      if ( lastToRemove != null ) {
        removeFinishedNodes( box, firstNode, lastToRemove, orphanLeafCount, widowLeafCount );
      }
    } else {
      // any kind of restricted element: We can only remove one element at a time and only if the
      // element is a orphan-leaf element. A orphan-leaf has no children that take part in the
      // widow/orphan constraint calculation and removing the leaf does not alter the result of the
      // calculation of the OrphanStep.

      RenderNode currentNode = box.getFirstChild();
      while ( currentNode != null && currentNode.isOpen() == false && checkFinishedForNode( currentNode ) ) {
        if ( ( currentNode.getY() + currentNode.getOverflowAreaHeight() ) > pageOffset ) {
          // we cant handle that. This node will be visible. So the current last
          // node is the one we can shrink ..
          break;
        }

        final RenderNode nodeForRemoval = currentNode;
        currentNode = currentNode.getNext();
        if ( isSafeForRemoval( nodeForRemoval ) ) {
          removeFinishedNodes( box, nodeForRemoval, nodeForRemoval, nodeForRemoval.getOrphanLeafCount(), nodeForRemoval
              .getWidowLeafCount() );
        }
      }

    }
    return true;
  }

  private boolean startTableSectionStyleBox( final RenderBox box ) {
    final int nodeType = box.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      return false;
    }

    boolean boxOutsideVisibleRange = ( box.getY() + box.getOverflowAreaHeight() ) <= pageOffset;
    final boolean safeForRemove = ( box.getParentWidowContexts() == 0 ) && boxOutsideVisibleRange;
    if ( safeForRemove || box.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED ) {
      // Next, search the last node that is fully invisible. We collapse all
      // invisible node into one big box for efficiency reasons. They wont be
      // visible anyway and thus the result will be the same as if they were
      // still alive ..
      final RenderNode firstNode = box.getFirstChild();
      RenderNode currentNode = firstNode;
      RenderNode lastToRemove = null;
      int orphanLeafCount = 0;
      int widowLeafCount = 0;
      while ( currentNode != null && currentNode.isOpen() == false && checkFinishedForNode( currentNode ) ) {
        if ( ( currentNode.getY() + currentNode.getOverflowAreaHeight() ) > pageOffset ) {
          // we cant handle that. This node will be visible. So the current last
          // node is the one we can shrink ..
          break;
        }

        if ( currentNode.getRowIndex() >= tableSectionContext.safeRows ) {
          break;
        }

        orphanLeafCount = currentNode.getOrphanLeafCount();
        widowLeafCount = currentNode.getWidowLeafCount();
        lastToRemove = currentNode;
        currentNode = currentNode.getNext();
      }

      if ( lastToRemove != null ) {
        removeFinishedNodes( box, firstNode, lastToRemove, orphanLeafCount, widowLeafCount );
      }
    } else {
      // any kind of restricted element: We can only remove one element at a time and only if the
      // element is a orphan-leaf element. A orphan-leaf has no children that take part in the
      // widow/orphan constraint calculation and removing the leaf does not alter the result of the
      // calculation of the OrphanStep.

      RenderNode currentNode = box.getFirstChild();
      while ( currentNode != null && currentNode.isOpen() == false && checkFinishedForNode( currentNode ) ) {
        if ( ( currentNode.getY() + currentNode.getOverflowAreaHeight() ) > pageOffset ) {
          // we cant handle that. This node will be visible. So the current last
          // node is the one we can shrink ..
          break;
        }
        if ( currentNode.getRowIndex() >= tableSectionContext.safeRows ) {
          break;
        }

        final RenderNode nodeForRemoval = currentNode;
        currentNode = currentNode.getNext();
        if ( isSafeForRemoval( nodeForRemoval ) ) {
          removeFinishedNodes( box, nodeForRemoval, nodeForRemoval, nodeForRemoval.getOrphanLeafCount(), nodeForRemoval
              .getWidowLeafCount() );
        }
      }

    }
    return true;
  }

  protected boolean checkFinishedForNode( final RenderNode currentNode ) {
    return currentNode.isFinishedPaginate();
  }

  private boolean isSafeForRemoval( final RenderNode node ) {
    if ( node.isOrphanLeaf() ) {
      return true;
    }
    if ( node.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED ) {
      return true;
    }

    if ( ( node.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      final RenderNode child = box.getFirstChild();
      if ( child != null && child == box.getLastChild() ) {
        if ( child.isOrphanLeaf() ) {
          return true;
        }
        if ( child.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED ) {
          return true;
        }
      }
    }
    return false;
  }

  private void removeFinishedNodes( final RenderBox box, final RenderNode firstNode, final RenderNode last,
      final int orphanLeafCount, final int widowLeafCount ) {
    if ( last.isOpen() ) {
      throw new IllegalStateException( "The last node is still open. We should not have come that far." );
    }

    if ( last == firstNode ) {
      if ( last.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
        // In this case, we can skip the replace-action below ..
        return;
      }
    }

    // So lets get started. We remove all nodes between (and inclusive)
    // node and last.
    final long width = box.getContentAreaX2() - box.getContentAreaX1();
    final long lastY2;
    if ( last.getNext() == null ) {
      lastY2 = last.getY() + last.getHeight();
    } else {
      // in case the next box had been shifted
      lastY2 = last.getNext().getY();
    }

    final long startOfBox;
    final RenderNode prev = firstNode.getPrev();
    if ( prev == null ) {
      final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
      final long insetsTop = sblp.getBorderTop() + box.getBoxDefinition().getPaddingTop();
      startOfBox = box.getY() + insetsTop;
    } else {
      startOfBox = prev.getY() + prev.getHeight();
    }
    final long height = lastY2 - startOfBox;

    if ( startOfBox + height > pageOffset ) {
      throw new IllegalStateException( "This finished node will intrude into the visible area." );
    }

    // make sure that the finished-box inherits the margins ..
    final long marginsTop = firstNode.getEffectiveMarginTop();
    final long marginsBottom = last.getEffectiveMarginBottom();
    final boolean breakAfter = isBreakAfter( last );

    RenderNode removeNode = firstNode;
    while ( removeNode != last ) {
      final RenderNode next = removeNode.getNext();
      if ( removeNode.isOpen() ) {
        throw new IllegalStateException( "A node is still open. We should not have come that far." );
      }
      box.remove( removeNode );
      removeNode = next;
    }

    final FinishedRenderNode replacement =
        new FinishedRenderNode( box.getContentAreaX1(), startOfBox, width, height, marginsTop, marginsBottom,
            breakAfter, orphanLeafCount, widowLeafCount );

    box.replaceChild( last, replacement );
    if ( replacement.getParent() != box ) {
      // return true;
      throw new IllegalStateException( "The replacement did not work." );
    }

    final long cachedY2;
    if ( last.getNext() == null ) {
      cachedY2 = last.getCachedY() + last.getCachedHeight();
    } else {
      cachedY2 = last.getNext().getCachedY();
    }

    final long newShift = lastY2 - cachedY2;
    if ( newShift > shiftOffset ) {
      shiftOffset = newShift;
      shiftNode = box.getInstanceId();
    }
  }

  private boolean isBreakAfter( final RenderNode node ) {
    if ( node.isBreakAfter() ) {
      return true;
    }

    if ( ( node.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      final RenderBox box = (RenderBox) node;
      final RenderNode lastChild = box.getLastChild();
      if ( lastChild != null ) {
        return isBreakAfter( lastChild );
      }
    }
    return false;
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return false;
  }

  protected boolean startAutoBox( final RenderBox box ) {
    if ( box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      if ( box.isContainsReservedContent() ) {
        // never clear out anything from reserved header or footer boxes. Never!
        return false;
      }

      if ( tableSectionContext.isProcessingUnsafe() ) {
        return false;
      }

      return startTableSectionStyleBox( box );
    }

    if ( box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return true;
    }

    if ( ( box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      return startBlockStyleBox( box );
    }
    return true;
  }
}
