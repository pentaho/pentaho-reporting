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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.BreakMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PageableBreakContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationState;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

public final class PaginationStep extends IterateVisualProcessStep
{
  private static final Log logger = LogFactory.getLog(PaginationStep.class);
  private boolean breakPending;
  private FindOldestProcessKeyStep findOldestProcessKeyStep;
  private PageBreakPositionList breakUtility;
  private ReportStateKey visualState;
  private BreakMarkerRenderBox breakIndicatorEncountered;
  private PaginationState paginationState;
  private long pageOffset;

  public PaginationStep()
  {
    findOldestProcessKeyStep = new FindOldestProcessKeyStep();
    breakUtility = new PageBreakPositionList();
  }

  public PaginationResult performPagebreak(final LogicalPageBox pageBox)
  {
    PaginationStepLib.assertProgress(pageBox);

    final long pageHeight = pageBox.getPageHeight();
    this.breakIndicatorEncountered = null;
    this.visualState = null;
    this.pageOffset = pageBox.getPageOffset();

    try
    {
      final long[] allCurrentBreaks = pageBox.getPhysicalBreaks(RenderNode.VERTICAL_AXIS);
      if (allCurrentBreaks.length == 0)
      {
        // No maximum height.
        throw new InvalidReportStateException("No page given. This is really bad.");
      }

      // Note: For now, we limit both the header and footer to a single physical
      // page. This safes me a lot of trouble for now.
      final long lastBreakLocal = allCurrentBreaks[allCurrentBreaks.length - 1];
      final long reservedHeight = PaginationStepLib.restrictPageAreaHeights(pageBox, allCurrentBreaks);
      if (reservedHeight >= lastBreakLocal)
      {
        // This is also bad. There will be no space left to print a single element.
        throw new InvalidReportStateException("Header and footer consume the whole page. No space left for normal-flow.");
      }

      PaginationStepLib.configureBreakUtility(breakUtility, pageBox, allCurrentBreaks, reservedHeight, lastBreakLocal);

      final long pageEnd = breakUtility.getLastMasterBreak();
      this.paginationState = new PaginationState(pageHeight, pageEnd);

      // now process all the other content (excluding the header and footer area)
      if (startBlockLevelBox(pageBox))
      {
        processBoxChilds(pageBox);
      }
      finishBlockLevelBox(pageBox);

      PaginationStepLib.assertProgress(pageBox);

      final PageableBreakContext context = pageBox.getBreakContext();
      if (context == null)
      {
        throw new IllegalStateException("After pagination, we have no break context. Why?");
      }

      final long masterBreak = breakUtility.getLastMasterBreak();
      final boolean overflow = breakIndicatorEncountered != null || pageBox.getHeight() > masterBreak;
      final boolean nextPageContainsContent = (pageBox.getHeight() > masterBreak);
      return new PaginationResult(breakUtility, overflow, nextPageContainsContent, visualState);
    }
    finally
    {
      this.paginationState = null;
      visualState = null;
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    final PageableBreakContext boxContext = PaginationStepLib.getBreakContext(box, true, false);
    final long shift = boxContext.getShift();

    PaginationStepLib.assertBlockPosition(box, shift);
    handleForcedBreakIndicator(box, boxContext);
    handleBlockLevelBoxFinishedMarker(box, shift);

    if (boxContext.isBreakSuspended() == false)
    {
      if (handleManualBreakOnBox(box, boxContext))
      {
        return true;
      }
    }

    // If this box does not cross any (major or minor) break, it may need no additional shifting at all.
    final RenderLength fixedPositionLength = box.getBoxDefinition().getFixedPosition();
    if (boxContext.isBreakSuspended() || RenderLength.AUTO.equals(fixedPositionLength))
    {
      return handleAutomaticPagebreak(box, boxContext, false);
    }

    // If you've come this far, this means, that your box has a fixed position defined.
    final long boxY = box.getY();
    final long shiftedBoxPosition = boxY + shift;
    final long fixedPositionResolved = fixedPositionLength.resolve(paginationState.getPageHeight(), 0);
    final long fixedPositionInFlow = breakUtility.computeFixedPositionInFlow(shiftedBoxPosition, fixedPositionResolved);
    if (fixedPositionInFlow < shiftedBoxPosition)
    {
      // ... but the fixed position is invalid, so treat it as non-defined.
      return handleAutomaticPagebreak(box, boxContext, false);
    }

    // The computed break seems to be valid.
    // Compute what happens if the whole box can fit on the current page.
    // We have an opportunity to optimize our processing by skipping all content if there are no
    // manual pagebreaks defined on one of the childs.
    if (breakUtility.isCrossingPagebreakWithFixedPosition
        (shiftedBoxPosition, box.getHeight(), fixedPositionResolved) == false)
    {
      final long fixedPositionDelta = fixedPositionInFlow - shiftedBoxPosition;
      final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
      if (breakIndicator == RenderBox.BreakIndicator.INDIRECT_MANUAL_BREAK)
      {
        // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
        // for now, we will only apply the ordinary shift.
        box.setY(fixedPositionInFlow);
        boxContext.setShift(shift + fixedPositionDelta);
        boxContext.setAppliedShift(shift + fixedPositionDelta);
        BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
        updateStateKey(box);
        return true;
      }
      else // if (breakIndicator == RenderBox.BreakIndicator.NO_MANUAL_BREAK)
      {
        // The whole box fits on the current page. However, we have to apply the shifting to move the box
        // to its defined fixed-position.
        //
        // As neither this box nor any of the children will cause a pagebreak, we can shift them and skip the processing
        // from here.
        BoxShifter.shiftBox(box, fixedPositionDelta);
        BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
        updateStateKeyDeep(box);
        return false;
      }
    }

    // The box will not fit on the current page.
    //
    // A box with a fixed position will always be printed at this position, even if it does not seem
    // to fit there. If we move the box, we would break the explicit layout constraint 'fixed-position' in
    // favour of an implicit one ('page-break: avoid').

    // Treat as if there is enough space available. Start the normal processing.
    final long fixedPositionDelta = fixedPositionInFlow - shiftedBoxPosition;
    boxContext.setShift(shift + fixedPositionDelta);
    boxContext.setAppliedShift(shift + fixedPositionDelta);
    box.setY(fixedPositionInFlow);
    BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
    updateStateKey(box);
    return true;
  }

  protected void processBlockLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
    if (breakPending == false && node.isBreakAfter())
    {
      breakPending = (true);
    }
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
    if (breakPending == false && box.isBreakAfter())
    {
      breakPending = (true);
    }
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = PaginationStepLib.getBreakContext(parentBox, false, false);
    parentContext.commitShift();
  }

  // At a later point, we have to do some real page-breaking here. We should check, whether the box fits, and should
  // shift the box if it doesnt.

  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(box, true, true);
    context.suspendBreaks();
    box.setY(box.getY() + context.getShift());
    return true;
  }

  protected void finishCanvasLevelBox(final RenderBox box)
  {
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = PaginationStepLib.getBreakContext(parentBox, false, false);
    parentContext.commitShift();
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    if (box.isCommited())
    {
      box.setFinishedPaginate(true);
    }

    final PageableBreakContext context = PaginationStepLib.getBreakContext(box, true, true);
    context.suspendBreaks();
    box.setY(box.getY() + context.getShift());
    return true;
  }

  protected void finishRowLevelBox(final RenderBox box)
  {
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = PaginationStepLib.getBreakContext(parentBox, false, false);
    parentContext.commitShift();
  }

  protected boolean startTableLevelBox(final RenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      final TableSectionRenderBox sectionRenderBox = (TableSectionRenderBox) box;
      final PageableBreakContext context = PaginationStepLib.getBreakContext(box, true, true);
      paginationState = new PaginationState(paginationState, false, !sectionRenderBox.isBody());

      switch (sectionRenderBox.getDisplayRole())
      {
        case HEADER:
        {
          // shift the header downwards,
          // 1. Check that this table actually breaks across the current page. Header position must be
          //    before the pagebox-offset. If not, return false, after the normal shifting.
          final long delta = pageOffset - (sectionRenderBox.getX() + context.getShift());
          if (delta <= 0)
          {
            BoxShifter.shiftBox(box, context.getShift());
            return false;
          }

          // 2. Shift the whole header downwards so that its upper edge matches the start of the page.
          //    return false afterwards.

          BoxShifter.shiftBox(box, delta);
          updateStateKeyDeep(box);
          context.setShift(context.getShift() + box.getHeight());
          BoxShifter.extendHeight(box.getParent(), box.getHeight());
          return false;
        }
        case FOOTER:
        {
          // shift the box and all children downwards. Suspend pagebreaks.
          BoxShifter.shiftBox(box, context.getShift());
          return false;
        }
        case BODY:
          return startBlockLevelBox(box);
        default:
          throw new IllegalArgumentException();
      }
    }
    else
    {
      paginationState = new PaginationState(paginationState, false, true);
      return true;
    }
  }

  protected void finishTableLevelBox(final RenderBox box)
  {
    paginationState = paginationState.pop();

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      final TableSectionRenderBox sectionRenderBox = (TableSectionRenderBox) box;
      if (sectionRenderBox.isBody() == false)
      {
        // never paginate inside a table-header or table-footer.
        final PageableBreakContext parentContext = PaginationStepLib.getBreakContext(box.getParent(), false, false);
        parentContext.commitShift();
        return;
      }
      finishBlockLevelBox(box);
    }
  }

  protected boolean startTableSectionLevelBox(final RenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      paginationState = new PaginationState(paginationState, box.isOpen(), false);
    }

    final PageableBreakContext context = PaginationStepLib.getBreakContext(box, true, true);
    context.suspendBreaks();

    // ignore all other break requests ..
    return startBlockLevelBox(box);
  }

  protected void finishTableSectionLevelBox(final RenderBox box)
  {
    finishBlockLevelBox(box);

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      paginationState = paginationState.pop();
    }
  }

  protected boolean startTableRowLevelBox(final RenderBox box)
  {
    return startRowLevelBox(box);
  }

  protected void finishTableRowLevelBox(final RenderBox box)
  {
    finishRowLevelBox(box);
  }

  protected boolean startTableCellLevelBox(final RenderBox box)
  {
    return startBlockLevelBox(box);
  }

  protected void finishTableCellLevelBox(final RenderBox box)
  {
    finishBlockLevelBox(box);
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(box, true, true);
    context.suspendBreaks();
    BoxShifter.shiftBox(box, context.getShift());
    return false;
  }

  protected void processInlineLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void finishInlineLevelBox(final RenderBox box)
  {
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = PaginationStepLib.getBreakContext(parentBox, false, false);
    parentContext.commitShift();
  }

  protected boolean startTableColLevelBox(final RenderBox box)
  {
    return false;
  }

  protected boolean startTableColGroupLevelBox(final RenderBox box)
  {
    return false;
  }

  protected void processCanvasLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processRowLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processOtherLevelChild(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processTableLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processTableRowLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processTableSectionLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processTableCellLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void processTableColLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = PaginationStepLib.getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  private void updateStateKey(final RenderBox box)
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
    if (y < paginationState.getPageEnd())
    {
      final ReportStateKey stateKey = box.getStateKey();
      if (stateKey != null)
      {
        this.visualState = stateKey;
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
    if (y >= paginationState.getPageEnd())
    {
      return;
    }

    final ReportStateKey reportStateKey = findOldestProcessKeyStep.find(box);
    if (reportStateKey != null)
    {
      this.visualState = reportStateKey;
    }
  }

  private boolean handleAutomaticPagebreak(final RenderBox box,
                                           final PageableBreakContext boxContext,
                                           final boolean assumePinned)
  {
    final long shift = boxContext.getShift();
    if (breakUtility.isCrossingPagebreak(box, shift) == false)
    {
      // The whole box fits on the current page. No need to do anything fancy.
      final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
      if (breakIndicator == RenderBox.BreakIndicator.INDIRECT_MANUAL_BREAK)
      {
        // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
        // for now, we will only apply the ordinary shift.
        final long boxY = box.getY();
        box.setY(boxY + shift);
        updateStateKey(box);
        return true;
      }
      else // if (breakIndicator == RenderBox.BreakIndicator.NO_MANUAL_BREAK)
      {
        // As neither this box nor any of the children will cause a pagebreak, we can shift them and skip the processing
        // from here.
        BoxShifter.shiftBox(box, shift);
        updateStateKeyDeep(box);
        return false;
      }
    }

    // At this point we know, that the box may cause some shifting. It crosses at least one minor or major pagebreak.
    // Right now, we are just evaluating the next break. In a future version, we could search all possible break
    // positions up to the next major break.
    final long boxY = box.getY();
    final long boxYShifted = boxY + shift;
    final long nextMinorBreak = breakUtility.findNextBreakPosition(boxYShifted);
    final long spaceAvailable = nextMinorBreak - boxYShifted;

    // This box sits directly on a pagebreak. This means, the page is empty, and there is no need for additional
    // shifting.
    if (spaceAvailable == 0)
    {
      box.setY(boxYShifted);
      updateStateKey(box);
      if (shift + box.getY() < nextMinorBreak)
      {
        box.markPinned(nextMinorBreak);
      }
      return true;
    }

    if (assumePinned || box.isPinned())
    {
      final long nextShift = box.getPinned() - boxY;
      final long shiftDelta = nextShift - shift;
      box.setY(boxY + nextShift);
      BoxShifter.extendHeight(box.getParent(), shiftDelta);
      boxContext.setShift(nextShift);
      boxContext.setAppliedShift(nextShift);
      updateStateKey(box);
      return true;
    }

    final long spaceConsumed = PaginationStepLib.computeNonBreakableBoxHeight(box);
    if (spaceAvailable < spaceConsumed)
    {
      // So we have not enough space to fullfill the layout-constraints. Be it so. Lets shift the box to the next
      // break.
      final long nextShift = nextMinorBreak - boxY;
      final long shiftDelta = nextShift - shift;
      box.setY(boxY + nextShift);
      BoxShifter.extendHeight(box.getParent(), shiftDelta);
      boxContext.setShift(nextShift);
      boxContext.setAppliedShift(nextShift);
      updateStateKey(box);
      if (shift + box.getY() < nextMinorBreak)
      {
        box.markPinned(nextMinorBreak);
      }
      return true;
    }

    // OK, there *is* enough space available. Start the normal processing
    box.setY(boxYShifted);
    updateStateKey(box);
    return true;
  }

  private boolean handleManualBreakOnBox(final RenderBox box,
                                         final PageableBreakContext boxContext)
  {
    final long shift = boxContext.getShift();
    final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
    // First check the simple cases:
    // If the box wants to break, then there's no point in waiting: Shift the box and continue.
    if (breakIndicator != RenderBox.BreakIndicator.DIRECT_MANUAL_BREAK && breakPending == false)
    {
      return false;
    }

    final RenderLength fixedPosition = box.getBoxDefinition().getFixedPosition();
    final long fixedPositionResolved = fixedPosition.resolve(paginationState.getPageHeight(), 0);
    final long boxY = box.getY();
    final long shiftedBoxY = boxY + shift;
    final long nextNonShiftedMajorBreak = breakUtility.findNextMajorBreakPosition(shiftedBoxY);
    final long fixedPositionOnNextPage =
        breakUtility.computeFixedPositionInFlow(nextNonShiftedMajorBreak, fixedPositionResolved);
    final long nextMajorBreak = Math.max(nextNonShiftedMajorBreak, fixedPositionOnNextPage);
    if (nextMajorBreak < shiftedBoxY)
    {
      // This band will be outside the last pagebreak. We can only shift it normally, but there is no way
      // that we could shift it to the final position yet.
      box.setY(boxY + shift);
    }
    else
    {
      final long nextShift = nextMajorBreak - boxY;
      final long shiftDelta = nextShift - shift;
      box.setY(boxY + nextShift);
      BoxShifter.extendHeight(box.getParent(), shiftDelta);
      boxContext.setShift(nextShift);
      boxContext.setAppliedShift(nextShift);
    }

    updateStateKey(box);
    final long pageEnd = paginationState.getPageEnd();
    if (shift + box.getY() < pageEnd)
    {
      box.markPinned(pageEnd);
    }
    breakPending = false;
    return true;
  }

  private void handleBlockLevelBoxFinishedMarker(final RenderBox box, final long shift)
  {
    if (box.isFinishedPaginate() != false)
    {
      return;
    }

    if (box.isCommited())
    {
      box.setFinishedPaginate(true);
    }
    else
    {
      final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
      if (sblp.isAvoidPagebreakInside() || sblp.getWidows() > 0 || sblp.getOrphans() > 0)
      {
        // Check, whether this box sits on a break-position. In that case, we can call that box finished as well.
        final long boxY = box.getY();
        final long nextMinorBreak = breakUtility.findNextBreakPosition(boxY + shift);
        final long spaceAvailable = nextMinorBreak - (boxY + shift);

        // This box sits directly on a pagebreak. No matter how much content we fill in the box, it will not move.
        // This makes this box a finished box.
        if (spaceAvailable == 0 || box.isPinned())
        {
          box.setFinishedPaginate(true);
        }
      }
      else
      {
        // This box defines no constraints that would cause a shift of it later in the process. We can treat it as
        // if it is finished already ..
        box.setFinishedPaginate(true);
      }
    }
  }

  private void handleForcedBreakIndicator(final RenderBox box, final PageableBreakContext boxContext)
  {
    final long shift = boxContext.getShift();
    if (boxContext.isBreakSuspended() == false &&
        breakIndicatorEncountered == null && box.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK)
    {
      // pin the parent ...
      // This beast is overly optimistic, so we can only pin if the box is within range
      final long pageEnd = paginationState.getPageEnd();
      if (shift + box.getY() < pageEnd)
      {
        box.markPinned(pageEnd);
      }

      breakIndicatorEncountered = (BreakMarkerRenderBox) box;
    }
  }

}
