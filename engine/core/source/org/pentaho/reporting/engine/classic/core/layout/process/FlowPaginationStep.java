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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

/**
 * The flow-pagination is a pagination step, where the page-boundaries cannot be determined beforehand. It only works
 * for infinite size pages and ignores all page-header and footers.
 * <p/>
 * The page-break list is updated on the fly while the report is paginated. A new break will only be added if the old
 * list did not contain the new break. It is guaranteed that only one break is added on every run.
 * <p/>
 * If complex compound layouts are required, this pagination step must be followed by a classical pagination step so
 * that boxes that overlap a break-position get shifted accordingly.
 *
 * @author Thomas Morgner
 */
public final class FlowPaginationStep extends IterateVisualProcessStep
{
  private class PaginationState
  {
    private PaginationState parent;
    private boolean rowOpen;
    private boolean suspended;
    private boolean suspendedTemporarily;

    private PaginationState(final PaginationState parent)
    {
      this.parent = parent;
      if (parent != null)
      {
        this.suspended = parent.isVisualStateCollectionSuspendedPermanently();
      }
    }

    private boolean isVisualStateCollectionSuspendedPermanently()
    {
      if (suspended)
      {
        return true;
      }

      if (rowOpen)
      {
        return true;
      }

      return false;
    }

    public boolean isVisualStateCollectionSuspended()
    {
      return suspendedTemporarily || isVisualStateCollectionSuspendedPermanently();
    }
  }

  private FindOldestProcessKeyStep findOldestProcessKeyStep;
  private boolean breakPending;
  private PageBreakPositionList breakUtility;
  private boolean breakAdded;
  private ReportStateKey visibleState;
  private long pageEnd;
  private PaginationState paginationState;

  public FlowPaginationStep()
  {
    breakUtility = new PageBreakPositionList();
    findOldestProcessKeyStep = new FindOldestProcessKeyStep();
  }

  public PaginationResult performPagebreak(final LogicalPageBox pageBox)
  {
    final RenderNode lastChild = pageBox.getLastChild();
    if (lastChild != null)
    {
      final long lastChildY2 = lastChild.getY() + lastChild.getHeight();
      if (lastChildY2 < pageBox.getHeight())
      {
        throw new IllegalStateException
            ("Assertation failed: Block layouting did not proceed: " + lastChildY2 + " < " + pageBox.getHeight());
      }
    }


    this.breakPending = false;
    this.breakAdded = false;
    this.visibleState = null;
    this.pageEnd = pageBox.getHeight();
    this.paginationState = new PaginationState(null);

    try
    {
      final PageBreakPositionList allPreviousBreak = pageBox.getAllVerticalBreaks();

      // Note: For now, we limit both the header and footer to a single physical
      // page. This safes me a lot of trouble for now.
      breakUtility.copyFrom(allPreviousBreak);

      // now process all the other content (excluding the header and footer area)
      if (startBlockLevelBox(pageBox))
      {
        processBoxChilds(pageBox);
      }
      finishBlockLevelBox(pageBox);

      if (lastChild != null)
      {
        final long lastChildY2 = lastChild.getY() + lastChild.getHeight();
        if (lastChildY2 < pageBox.getHeight())
        {
          throw new IllegalStateException
              ("Assertation failed: Pagination violated block-constraints: " + lastChildY2 + " < " + pageBox.getHeight());
        }
      }

      final long masterBreak = breakUtility.getLastMasterBreak();
      final boolean nextPageContainsContent = (pageBox.getHeight() > masterBreak);
      return new PaginationResult(breakUtility, breakAdded, nextPageContainsContent, visibleState);
    }
    finally
    {
      this.paginationState = null;
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK)
    {
      final long boxY = box.getY();
      if (breakAdded == false &&
          // boxY != pageEnd &&
          boxY > breakUtility.getLastMasterBreak())
      {
        // This box will cause a new break. Add it.
        if (breakUtility.getLastMasterBreak() != boxY)
        {
          breakAdded = true;
          breakUtility.addMajorBreak(box.getY(), 0);
        }
      }
      breakPending = false;
      return false;
    }

    // First check the simple cases:
    // If the box wants to break, then there's no point in waiting: Shift the box and continue.
    if (breakIndicator == RenderBox.BreakIndicator.DIRECT_MANUAL_BREAK || breakPending)
    {
      // find the next major break and shift the box to this position.
      // update the 'shift' to reflect this new change. Process the contents of this box as well, as the box may
      // have additional breaks inside (or may overflow, or whatever ..).
      final long boxY = box.getY();
      if (breakAdded == false &&
//          boxY != pageEnd && // damn! we cannot deal with breaks on the page-end yet.
          boxY > breakUtility.getLastMasterBreak())
      {
        if (breakUtility.getLastMasterBreak() != boxY)
//        if (boxY != pageEnd)
        {
          // This box will cause a new break. Add it.
          breakAdded = true;
          breakUtility.addMajorBreak(box.getY(), 0);
        }
      }
      breakPending = false;
      return true;
    }

    if (breakIndicator == RenderBox.BreakIndicator.NO_MANUAL_BREAK)
    {
      // As neither this box nor any of the children will cause a pagebreak, skip the processing of the childs.
      if (breakAdded == false)
      {
        updateStateKeyDeep(box);
      }
      return false;
    }

    // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
    // for now, we will only apply the ordinary shift.
    if (breakAdded == false)
    {
      updateStateKey(box);
    }
    return true;
  }

  protected boolean startTableRowLevelBox(final RenderBox box)
  {
    return false;
  }

  protected boolean startTableColGroupLevelBox(final RenderBox box)
  {
    return false;
  }

  protected boolean startTableColLevelBox(final RenderBox box)
  {
    return false;
  }

  protected boolean startTableLevelBox(final RenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      final TableSectionRenderBox sectionRenderBox = (TableSectionRenderBox) box;

      paginationState = new PaginationState(paginationState);
      paginationState.suspended |= !sectionRenderBox.isBody();
      paginationState.suspendedTemporarily = true;

      if (sectionRenderBox.isBody() == false)
      {
        // never paginate inside a table-header or table-footer.
        return false;
      }
    }
    else
    {
      paginationState = new PaginationState(paginationState);
      paginationState.suspended = true;
    }

    return startBlockLevelBox(box);
  }

  protected void finishTableLevelBox(final RenderBox box)
  {
    paginationState = paginationState.parent;

    if (BandStyleKeys.LAYOUT_TABLE_BODY.equals(box.getStyleSheet().getStyleProperty(BandStyleKeys.LAYOUT)) == false)
    {
      return;
    }

    if (breakPending == false && box.isBreakAfter())
    {
      breakPending = true;
    }
  }

  protected boolean startTableSectionLevelBox(final RenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      paginationState = new PaginationState(paginationState);
      paginationState.rowOpen = box.isOpen();
    }

    // ignore all other break requests ..
    return startBlockLevelBox(box);
  }

  protected void finishTableSectionLevelBox(final RenderBox box)
  {
    if (breakPending == false && box.isBreakAfter())
    {
      breakPending = true;
    }

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      paginationState = paginationState.parent;
    }
  }

  protected void processCanvasLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processBlockLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processRowLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processTableLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processTableRowLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processTableSectionLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processTableCellLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processTableColLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  protected void processTableColGroupLevelNode(final RenderNode node)
  {
    if (breakAdded == false && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      updateStateKey(node);
    }
  }

  private void updateStateKey(final RenderNode box)
  {
    if (paginationState.isVisualStateCollectionSuspended())
    {
      return;
    }

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE)
    {
      return;
    }

    final long y = box.getY();
    if (y < (pageEnd))
    {
      final ReportStateKey stateKey = box.getStateKey();
      if (stateKey != null)
      {
        this.visibleState = stateKey;
      }
    }
  }

  private void updateStateKeyDeep(final RenderBox box)
  {
    if (paginationState.isVisualStateCollectionSuspended())
    {
      return;
    }

    final long y = box.getY();
    if (y >= (pageEnd))
    {
      return;
    }

    final ReportStateKey reportStateKey = findOldestProcessKeyStep.find(box);
    if (reportStateKey != null)
    {
      this.visibleState = reportStateKey;
    }
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
    if (breakPending == false && box.isBreakAfter())
    {
      breakPending = true;
    }
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    return false;
  }

  // At a later point, we have to do some real page-breaking here. We should check, whether the box fits, and should
  // shift the box if it doesnt.
  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    return false;
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    return false;
  }
}
