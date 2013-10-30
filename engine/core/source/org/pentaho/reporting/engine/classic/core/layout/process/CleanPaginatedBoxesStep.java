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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBoxNonAutoIterator;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;


/**
 * This step must not remove boxes that have a manual break attached.
 *
 * @author Thomas Morgner
 */
public final class CleanPaginatedBoxesStep extends IterateStructuralProcessStep
{
  private long pageOffset;
  private long shiftOffset;
  private InstanceID shiftNode;
  private PageBreakPositionList allVerticalBreaks;

  public CleanPaginatedBoxesStep()
  {
  }

  public long compute(final LogicalPageBox pageBox)
  {
    shiftOffset = 0;
    pageOffset = pageBox.getPageOffset();
    allVerticalBreaks = pageBox.getAllVerticalBreaks();
    if (startBlockBox(pageBox))
    {
      // not processing the header and footer area: they are 'out-of-context' bands
      processBoxChilds(pageBox);
    }
    finishBlockBox(pageBox);
    //Log.debug ("ShiftOffset after clean: " + shiftOffset);
    return shiftOffset;
  }

  public InstanceID getShiftNode()
  {
    return shiftNode;
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    // we do not process the paragraph lines. This should have been done
    // in the startblock thing and they get re-added anyway as long as the
    // paragraph is active.
  }

  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    return false;
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return true;
  }

  protected boolean startTableColumnGroupBox(final TableColumnGroupNode box)
  {
    return false;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return startBlockStyleBox(box);
  }

  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    if (box.getDisplayRole() == TableSectionRenderBox.Role.BODY)
    {
      return startCleanTableRowBoxesFromSection(box);
    }

    return false;
  }

  protected boolean startTableCellBox(final TableCellRenderBox box)
  {
    return startBlockBox(box);
  }

  private Boolean filterNonRemovableStates(final RenderBox box)
  {
    if (box.isFinishedPaginate() == false)
    {
      return Boolean.TRUE;
    }

    final RenderNode firstNode = box.getFirstChild();
    if (firstNode == null)
    {
      // The cell is empty ..
      return Boolean.FALSE;
    }

    final long nodeY = firstNode.getY();
    if (nodeY > pageOffset)
    {
      // This box will be visible or will be processed in the future.
      return Boolean.FALSE;
    }

    if (firstNode.isOpen())
    {
      return Boolean.TRUE;
    }
/*
    if ((nodeY + firstNode.getOverflowAreaHeight()) > pageOffset)
    {
      // this box will span to the next page and cannot be removed ...
      return true;
    }
    */
    return null;
  }

  private boolean startCleanTableRowBoxesFromSection(final TableSectionRenderBox box)
  {
    if (box.isFinishedPaginate() == false)
    {
      return true;
    }

    final Boolean filterResult = filterNonRemovableStates(box);
    if (filterResult != null)
    {
      return filterResult.booleanValue();
    }

    // todo: PRD-3857 - this model of cleaning out rows fails when faced with auto-boxes.

    final TableRowModel rowModel = box.getRowModel();
    final RenderBoxNonAutoIterator rows = new RenderBoxNonAutoIterator(box);
    RenderNode lastNode = null;
    while (rows.hasNext())
    {
      final RenderNode rowNode = rows.next();
      if (rowNode.isFinishedPaginate() == false)
      {
        break;
      }

      if (rowNode.isOpen())
      {
        // as long as a box is open, it can grow and therefore it cannot be
        // removed ..
        break;
      }

      if (rowNode.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
      {
        final TableRowRenderBox rowRenderBox = (TableRowRenderBox) rowNode;
        final long height = rowModel.getValidatedRowSpanSize(rowRenderBox.getRowIndex());
        if (rowNode.getY() + height > pageOffset)
        {
          break;
        }
        lastNode = rowNode;
      }
    }

    if (lastNode != null)
    {
      final RenderBox parent = lastNode.getParent();
      final RenderNode firstChild = parent.getFirstChild();
      // todo:
      removeFinishedNodes(parent, firstChild, lastNode, lastNode.isOrphanLeaf());
    }

    return true;
  }

  private boolean startBlockStyleBox(final RenderBox box)
  {
    final int nodeType = box.getLayoutNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      return false;
    }

    if (box.isFinishedPaginate() == false)
    {
      return true;
    }

    final boolean safeForRemove = box.getParentWidowContexts() == 0 && (box.getY() + box.getOverflowAreaHeight()) <= pageOffset;
    if (safeForRemove || box.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED)
    {
      // Next, search the last node that is fully invisible. We collapse all
      // invisible node into one big box for efficiency reasons. They wont be
      // visible anyway and thus the result will be the same as if they were
      // still alive ..
      final RenderNode firstNode = box.getFirstChild();
      RenderNode currentNode = firstNode;
      RenderNode lastToRemove = null;

      while (currentNode != null && currentNode.isOpen() == false)
      {
        if ((currentNode.getY() + currentNode.getOverflowAreaHeight()) > pageOffset)
        {
          // we cant handle that. This node will be visible. So the current last
          // node is the one we can shrink ..
          break;
        }

        lastToRemove = currentNode;
        currentNode = currentNode.getNext();
      }

      if (lastToRemove != null)
      {
        removeFinishedNodes(box, firstNode, lastToRemove, false);
      }
    }
    else
    {
      // any kind of restricted element: We can only remove one element at a time and only if the
      // element is a orphan-leaf element. A orphan-leaf has no children that take part in the
      // widow/orphan constraint calculation and removing the leaf does not alter the result of the
      // calculation of the OrphanStep.

      RenderNode currentNode = box.getFirstChild();
      while (currentNode != null && currentNode.isOpen() == false)
      {
        if ((currentNode.getY() + currentNode.getOverflowAreaHeight()) > pageOffset)
        {
          // we cant handle that. This node will be visible. So the current last
          // node is the one we can shrink ..
          break;
        }

        final RenderNode nodeForRemoval = currentNode;
        currentNode = currentNode.getNext();
        if (isSafeForRemoval(nodeForRemoval))
        {
          removeFinishedNodes(box, nodeForRemoval, nodeForRemoval, true);
        }
      }

    }
    return true;
  }

  private boolean isSafeForRemoval(final RenderNode node)
  {
    if (node.isOrphanLeaf())
    {
      return true;
    }
    if (node.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED)
    {
      return true;
    }

    if ((node.getNodeType() & LayoutNodeTypes.MASK_BOX) == LayoutNodeTypes.MASK_BOX)
    {
      final RenderBox box = (RenderBox) node;
      final RenderNode child = box.getFirstChild();
      if (child != null && child == box.getLastChild())
      {
        if (child.isOrphanLeaf())
        {
          return true;
        }
        if (child.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED)
        {
          return true;
        }
      }
    }
    return false;
  }

  private void removeFinishedNodes(final RenderBox box,
                                   final RenderNode firstNode,
                                   final RenderNode last,
                                   final boolean leaf)
  {
    if (last.isOpen())
    {
      throw new IllegalStateException("The last node is still open. We should not have come that far.");
    }

    if (last == firstNode)
    {
      if (last.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
      {
        // In this case, we can skip the replace-action below ..
        return;
      }
    }

    // So lets get started. We remove all nodes between (and inclusive)
    // node and last.
    final long width = box.getContentAreaX2() - box.getContentAreaX1();
    final long lastY2;
    if (last.getNext() == null)
    {
      lastY2 = last.getY() + last.getHeight();
    }
    else
    {
      // in case the next box had been shifted
      lastY2 = last.getNext().getY();
    }

    final long startOfBox;
    final RenderNode prev = firstNode.getPrev();
    if (prev == null)
    {
      final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
      final long insetsTop = sblp.getBorderTop() + box.getBoxDefinition().getPaddingTop();
      startOfBox = box.getY() + insetsTop;
    }
    else
    {
      startOfBox = prev.getY() + prev.getHeight();
    }
    final long height = lastY2 - startOfBox;

    if (startOfBox + height > pageOffset)
    {
      throw new IllegalStateException("This finished node will intrude into the visible area.");
    }

    // make sure that the finished-box inherits the margins ..
    final long marginsTop = firstNode.getEffectiveMarginTop();
    final long marginsBottom = last.getEffectiveMarginBottom();
    final boolean breakAfter = isBreakAfter(last);

    RenderNode removeNode = firstNode;
    while (removeNode != last)
    {
      final RenderNode next = removeNode.getNext();
      if (removeNode.isOpen())
      {
        throw new IllegalStateException("A node is still open. We should not have come that far.");
      }
      box.remove(removeNode);
      removeNode = next;
    }

    final FinishedRenderNode replacement = new FinishedRenderNode
        (box.getContentAreaX1(), startOfBox, width, height, marginsTop, marginsBottom, breakAfter, leaf);

    box.replaceChild(last, replacement);
    if (replacement.getParent() != box)
    {
//      return true;
      throw new IllegalStateException("The replacement did not work.");
    }

    final long cachedY2;
    if (last.getNext() == null)
    {
      cachedY2 = last.getCachedY() + last.getCachedHeight();
    }
    else
    {
      cachedY2 = last.getNext().getCachedY();
    }

    final long newShift = lastY2 - cachedY2;
    if (newShift > shiftOffset)
    {
      shiftOffset = newShift;
      shiftNode = box.getInstanceId();
    }
  }

  private boolean isBreakAfter(final RenderNode node)
  {
    if (node.isBreakAfter())
    {
      return true;
    }

    if ((node.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      final RenderBox box = (RenderBox) node;
      final RenderNode lastChild = box.getLastChild();
      if (lastChild != null)
      {
        return isBreakAfter(lastChild);
      }
    }
    return false;
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    return false;
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      return startBlockStyleBox(box);
    }
    return true;
  }
}
