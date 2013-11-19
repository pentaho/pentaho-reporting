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
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * Removed finished block-boxes. The boxes have to be marked as 'finished' by the flow output target or nothing will be
 * removed at all. The boxes marked as finished will be replaced by 'FinishedRenderNodes'. This step preserves nodes
 * that have pagebreaks.
 *
 * @author Thomas Morgner
 */
public final class CleanFlowBoxesStep extends IterateStructuralProcessStep
{
  private static class TableSectionContext
  {
    private TableSectionContext context;
    private int safeRows;
    private int expectedNextRowNumber;

    private TableSectionContext(final TableSectionContext context)
    {
      this.context = context;
    }

    public TableSectionContext pop()
    {
      return context;
    }

    public boolean isProcessingUnsafe()
    {
      return expectedNextRowNumber <= safeRows;
    }
  }

  private static class BoxContext
  {
    private BoxContext parent;
    private boolean finished;

    private BoxContext(final BoxContext parent, final boolean finished)
    {
      this.parent = parent;
      this.finished = finished;
    }

    public void markUnfinished()
    {
      this.finished = false;
    }

    public boolean isFinished()
    {
      return finished;
    }

    public BoxContext pop()
    {
      if (parent != null)
      {
        if (this.finished == false)
        {
          parent.markUnfinished();
        }
      }
      return parent;
    }
  }

  private ReportStateKey lastSeenStateKey;
  private BoxContext boxContext;
  private TableSectionContext tableSectionContext;
  private long pageOffset;

  public CleanFlowBoxesStep()
  {
  }

  public void compute(final LogicalPageBox pageBox)
  {
    this.boxContext = new BoxContext(null, true);
    this.pageOffset = pageBox.getPageOffset();
    try
    {
      if (startBlockBox(pageBox))
      {
        // not processing the header and footer area: they are 'out-of-context' bands
        processBoxChilds(pageBox);
      }
      finishBlockBox(pageBox);
    }
    finally
    {
      this.boxContext = null;
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    // we do not process the paragraph lines. This should have been done
    // in the startblock thing and they get re-added anyway as long as the
    // paragraph is active.
  }

  protected boolean startRowBox(final RenderBox box)
  {
    // it is guaranteed that the finished flag is only set to true, if the box is closed.
    this.boxContext = new BoxContext(this.boxContext, box.isFinishedTable() && box.isCommited());

    final ReportStateKey stateKey = box.getStateKey();
    if (stateKey != null)
    {
      lastSeenStateKey = stateKey;
    }

    return true;
  }

  protected void finishRowBox(final RenderBox box)
  {
    box.setDeepFinished(boxContext.isFinished());
    boxContext = boxContext.pop();
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    this.boxContext = new BoxContext(this.boxContext, box.isFinishedTable() && box.isCommited());

    final ReportStateKey stateKey = box.getStateKey();
    if (stateKey != null)
    {
      lastSeenStateKey = stateKey;
    }

    return true;
  }

  protected void finishCanvasBox(final CanvasRenderBox box)
  {
    box.setDeepFinished(boxContext.isFinished());
    boxContext = boxContext.pop();
  }

  // We cannot clear the box until we have verified that all childs of that box have been cleared.

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return startBlockStyleBox(box);
  }

  private boolean startBlockStyleBox(final RenderBox box)
  {
    this.boxContext = new BoxContext(this.boxContext, box.isFinishedTable() && box.isCommited());

    final ReportStateKey stateKey = box.getStateKey();
    if (stateKey != null)
    {
      lastSeenStateKey = stateKey;
    }

    return true;
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    finishBlockStyleBox(box);
  }

  private void finishBlockStyleBox(final RenderBox box)
  {
    boolean finished = boxContext.isFinished();
    box.setDeepFinished(finished);
    boxContext = boxContext.pop();

    final RenderNode first = box.getFirstChild();
    if (first == null)
    {
      return;
    }
    if (first.isDeepFinishedTable() == false)
    {
      return;
    }

    RenderNode last = first;
    while (true)
    {
      final RenderNode next = last.getNext();
      if (next == null)
      {
        break;
      }
      if (next.isDeepFinishedTable() == false)
      {
        break;
      }
      last = next;
    }

    if (last == first && (first.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE))
    {
      // In this case, we can skip the replace-action below ..
      return;
    }

    // So lets get started. We remove all nodes between (and inclusive)
    // node and last.
    final long nodeY = first.getY();
    final long width = box.getContentAreaX2() - box.getContentAreaX1();
    final long lastY2 = last.getY() + last.getOverflowAreaHeight();
    final long height = lastY2 - nodeY;

    // make sure that the finished-box inherits the margins ..
    final long marginsTop = first.getEffectiveMarginTop();
    final long marginsBottom = last.getEffectiveMarginBottom();
    final boolean breakAfter = last.isBreakAfter();
    final FinishedRenderNode replacement =
        new FinishedRenderNode(box.getContentAreaX1(), nodeY,
            width, height, marginsTop, marginsBottom, breakAfter, false, lastSeenStateKey);

    RenderNode removeNode = first;
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

    if (last.isOpen())
    {
      throw new IllegalStateException("The last node is still open. We should not have come that far.");
    }

    box.replaceChild(last, replacement);
    if (replacement.getParent() != box)
    {
      throw new IllegalStateException("The replacement did not work.");
    }
  }

  protected boolean startTableColumnGroupBox(final TableColumnGroupNode box)
  {
    return false;
  }

  protected boolean startTableRowBox(final TableRowRenderBox box)
  {
    this.boxContext = new BoxContext(this.boxContext, false);
    return true;
  }


  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    tableSectionContext = new TableSectionContext(tableSectionContext);

    if (box.getDisplayRole() == TableSectionRenderBox.Role.BODY)
    {
      CleanTableRowsPreparationStep preparationStep = new CleanTableRowsPreparationStep();
      tableSectionContext.safeRows = preparationStep.process(box, pageOffset);
      tableSectionContext.expectedNextRowNumber = preparationStep.getFirstRowEncountered();

      if (tableSectionContext.isProcessingUnsafe())
      {
        return false;
      }
      return startBlockStyleBox(box);
    }

    return false;
  }

  protected void finishTableSectionBox(final TableSectionRenderBox box)
  {
    if (box.getDisplayRole() == TableSectionRenderBox.Role.BODY)
    {
      if (tableSectionContext.isProcessingUnsafe() == false)
      {
        finishBlockStyleBox(box);
      }
    }
    tableSectionContext = tableSectionContext.pop();
  }

  protected void processOtherNode(final RenderNode node)
  {
    if (node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      final ReportStateKey stateKey = node.getStateKey();
      if (stateKey != null)
      {
        lastSeenStateKey = stateKey;
      }
    }
  }

  protected boolean startTableCellBox(final TableCellRenderBox box)
  {
    return startBlockStyleBox(box);
  }

  protected void finishTableCellBox(final TableCellRenderBox box)
  {
    finishBlockStyleBox(box);
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    final int layoutNodeType = box.getLayoutNodeType();
    final int filteredType = layoutNodeType & LayoutNodeTypes.MASK_BASIC_BOX_TYPE;
    if (filteredType == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      return startBlockStyleBox(box);
    }
    // todo: Cleaning within tables ...
    return false;
  }

  protected void finishAutoBox(final RenderBox box)
  {
    final int layoutNodeType = box.getLayoutNodeType();
    final int filteredType = layoutNodeType & LayoutNodeTypes.MASK_BASIC_BOX_TYPE;
    if (filteredType == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      finishBlockStyleBox(box);
    }
  }
}
