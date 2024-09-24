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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.FlowPageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositions;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.util.FlowPaginationTableState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.InitialPaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationShiftStatePool;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * This class uses the concept of shifting to push boxes, which otherwise do not fit on the current page, over the
 * page-boundary of the next page.
 * <p/>
 * We have two shift positions. The normal shift denotes artificial paddings, inserted into the flow where needed to
 * move content to the next page. The header-shift is inserted when a repeatable table-header is processed. This header
 * reserves a virtual padding area in the infinite-canvas flow to push the next assumed pagebreak to the y2-position of
 * the header. A header-shift modifies the pin-position on a box, and modifies where pagebreaks are detected.
 */
public final class FlowPaginationStep extends IterateVisualProcessStep {
  private static final Log logger = LogFactory.getLog( FlowPaginationStep.class );
  private boolean breakPending;
  private FindOldestProcessKeyStep findOldestProcessKeyStep;
  private FlowPageBreakPositionList basePageBreakList;
  private ReportStateKey visualState;
  private FlowPaginationTableState paginationTableState;
  private PaginationShiftState shiftState;
  private PaginationShiftStatePool shiftStatePool;
  private long pageOffsetKey;
  private boolean unresolvedWidowReferenceEncountered;
  private long recordedPageBreakPosition;
  private boolean recordedPageBreakPositionIsForced;

  public FlowPaginationStep() {
    findOldestProcessKeyStep = new FindOldestProcessKeyStep();
    basePageBreakList = new FlowPageBreakPositionList();
    shiftStatePool = new PaginationShiftStatePool();
  }

  public PaginationResult performPagebreak( final LogicalPageBox pageBox ) {
    getEventWatch().start();
    getSummaryWatch().start();
    PaginationStepLib.assertProgress( pageBox );

    this.unresolvedWidowReferenceEncountered = false;
    this.visualState = null;
    this.pageOffsetKey = pageBox.getPageOffset();
    this.shiftState = new InitialPaginationShiftState();
    this.breakPending = false;
    this.recordedPageBreakPosition = 0;
    this.recordedPageBreakPositionIsForced = false;

    try {
      // do not add a pagebreak for the physical end.
      final PageBreakPositionList allPreviousBreak = pageBox.getAllVerticalBreaks();
      basePageBreakList.copyFrom( allPreviousBreak );

      this.paginationTableState = new FlowPaginationTableState( pageBox.getPageOffset(), basePageBreakList );

      // now process all the other content (excluding the header and footer area)
      if ( startBlockLevelBox( pageBox ) ) {
        processBoxChilds( pageBox );
      }
      finishBlockLevelBox( pageBox );

      PaginationStepLib.assertProgress( pageBox );

      // reset pagebreaks to state before we performed a pagebreak.
      basePageBreakList.copyFrom( allPreviousBreak );

      final boolean pagebreakEncountered =
          recordedPageBreakPosition != 0
              && ( recordedPageBreakPositionIsForced || recordedPageBreakPosition != pageBox.getHeight() );
      final boolean nextPageContainsContent;
      if ( pagebreakEncountered == false ) {
        nextPageContainsContent = false;
      } else {
        basePageBreakList.addMajorBreak( recordedPageBreakPosition, 0 );
        if ( recordedPageBreakPositionIsForced ) {
          nextPageContainsContent = false;
        } else {
          nextPageContainsContent = ( pageBox.getHeight() > recordedPageBreakPosition );
        }
      }

      return new PaginationResult( basePageBreakList, pagebreakEncountered, nextPageContainsContent, visualState );
    } finally {
      getEventWatch().stop();
      getSummaryWatch().stop( true );
      this.paginationTableState = null;
      this.visualState = null;
      this.shiftState = null;
    }
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }

  protected boolean startBlockLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    final boolean retval = handleStartBlockLevelBox( box );
    installTableContext( box );
    return retval;
  }

  private boolean handleStartBlockLevelBox( final RenderBox box ) {
    this.shiftState = shiftStatePool.create( box, shiftState );
    final long shift = shiftState.getShiftForNextChild();

    if ( box.isWidowBox() ) {
      unresolvedWidowReferenceEncountered = true;
    }

    if ( unresolvedWidowReferenceEncountered ) {
      // once we have hit a unresolved widow box, we cannot process the page any further
      // we have to wait until the box is closed to know whether the widow-constraint can be
      // fulfilled.
      BoxShifter.shiftBox( box, shift );
      return false;
    }

    PaginationStepLib.assertBlockPosition( box, shift );

    if ( shiftState.isManualBreakSuspended() == false ) {
      if ( handleManualBreakOnBox( box, shiftState, breakPending ) ) {
        breakPending = false;
        if ( logger.isDebugEnabled() ) {
          logger.debug( "pending page-break or manual break: " + box );
        }
        return true;
      }
      breakPending = false;
    }

    // If this box does not cross any (major or minor) break, it may need no additional shifting at all.
    return handleAutomaticPagebreak( box, shiftState );
  }

  protected void processBlockLevelNode( final RenderNode node ) {
    final long shift = shiftState.getShiftForNextChild();
    node.setY( node.getY() + shift );
    if ( breakPending == false && node.isBreakAfter() ) {
      breakPending = paginationTableState.isOnPageStart( node.getY() ) == false;
      if ( breakPending ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "BreakPending True for Node:isBreakAfter: " + node );
        }
      }
    }
  }

  protected void finishBlockLevelBox( final RenderBox box ) {
    uninstallTableContext( box );

    if ( breakPending == false && box.isBreakAfter() ) {
      breakPending = paginationTableState.isOnPageStart( box.getY() + box.getHeight() ) == false;
      if ( breakPending ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "BreakPending True for Box:isBreakAfter: " + box );
        }
      }
    }

    shiftState = shiftState.pop( box.getInstanceId() );
  }

  // At a later point, we have to do some real page-breaking here. We should check, whether the box fits, and should
  // shift the box if it doesnt.

  protected boolean startCanvasLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    installTableContext( box );

    shiftState = shiftStatePool.create( box, shiftState );
    shiftState.suspendManualBreaks();
    box.setY( box.getY() + shiftState.getShiftForNextChild() );
    return true;
  }

  protected void finishCanvasLevelBox( final RenderBox box ) {
    shiftState = shiftState.pop( box.getInstanceId() );
    uninstallTableContext( box );
  }

  protected boolean startRowLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    installTableContext( box );

    shiftState = shiftStatePool.create( box, shiftState );
    shiftState.suspendManualBreaks();
    box.setY( box.getY() + shiftState.getShiftForNextChild() );
    return true;
  }

  protected void finishRowLevelBox( final RenderBox box ) {
    shiftState = shiftState.pop( box.getInstanceId() );
    uninstallTableContext( box );
  }

  protected boolean startTableLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      final TableSectionRenderBox sectionRenderBox = (TableSectionRenderBox) box;
      switch ( sectionRenderBox.getDisplayRole() ) {
        case HEADER: {
          shiftState = shiftStatePool.create( box, shiftState );

          paginationTableState = new FlowPaginationTableState( paginationTableState );
          paginationTableState.suspendVisualStateCollection( true );

          startTableHeaderSection( box, sectionRenderBox );
          return false;
        }
        case FOOTER: {
          shiftState = shiftStatePool.create( box, shiftState );

          paginationTableState = new FlowPaginationTableState( paginationTableState );
          paginationTableState.suspendVisualStateCollection( true );

          // shift the box and all children downwards. Suspend pagebreaks.
          final long contextShift = shiftState.getShiftForNextChild();
          BoxShifter.shiftBox( box, contextShift );
          return false;
        }
        case BODY:
          return startBlockLevelBox( box );
        default:
          throw new IllegalArgumentException();
      }
    } else {
      return true;
    }
  }

  private void startTableHeaderSection( final RenderBox box, final TableSectionRenderBox sectionRenderBox ) {
    final long contextShift = shiftState.getShiftForNextChild();
    // shift the header downwards,
    // 1. Check that this table actually breaks across the current page. Header position must be
    // before the pagebox-offset. If not, return false, after the normal shifting.
    final long pageOffset = paginationTableState.getPageOffset();
    final long delta = pageOffset - ( sectionRenderBox.getY() + contextShift );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "PageOffset: " + delta );
    }
    if ( delta <= 0 ) {
      BoxShifter.shiftBox( box, contextShift );
      if ( logger.isDebugEnabled() ) {
        logger.debug( "HEADER NOT SHIFTED; DELTA = " + delta + " -> " + contextShift );
      }
      sectionRenderBox.setHeaderShift( pageOffsetKey, 0 );
      return;
    }

    // 2. Shift the whole header downwards so that its upper edge matches the start of the page.
    // return false afterwards.

    if ( logger.isDebugEnabled() ) {
      logger.debug( "HEADER SHIFTED; DELTA = " + delta + " -> " + contextShift );
    }
    long headerShift = sectionRenderBox.getHeaderShift( pageOffsetKey );
    if ( headerShift == 0 ) {
      final long previousPageOffset =
          paginationTableState.getBreakPositions().findPageStartPositionForPageEndPosition( pageOffset );
      headerShift = sectionRenderBox.getHeaderShift( previousPageOffset ) + box.getHeight();
      if ( logger.isDebugEnabled() ) {
        logger.debug( "HeaderShift: " + headerShift + " <=> " + pageOffset + " ; prevOffset=" + previousPageOffset );
      }
      sectionRenderBox.setHeaderShift( pageOffsetKey, headerShift );
    } else {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Existing HeaderShift: " + headerShift + " <=> " + pageOffset );
      }
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug( "Table-Height before extension: " + box.getParent().getHeight() );
    }
    BoxShifter.shiftBox( box, delta );
    updateStateKeyDeep( box );
    shiftState.increaseShift( headerShift );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "Table-Height after extension: " + box.getParent().getHeight() );
    }
  }

  protected void finishTableLevelBox( final RenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      final TableSectionRenderBox sectionRenderBox = (TableSectionRenderBox) box;
      switch ( sectionRenderBox.getDisplayRole() ) {
        case HEADER:
          shiftState = shiftState.pop( box.getInstanceId() );
          paginationTableState = paginationTableState.pop();
          paginationTableState.defineArtificialPageStart( box.getHeight() + paginationTableState.getPageOffset() );
          break;
        case FOOTER:
          shiftState = shiftState.pop( box.getInstanceId() );
          paginationTableState = paginationTableState.pop();
          break;
        case BODY:
          finishBlockLevelBox( box );
          break;
        default:
          throw new IllegalStateException();
      }
      return;
    }

    finishBlockLevelBox( box );
  }

  protected boolean startTableSectionLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
      if ( box.isOpen() ) {
        paginationTableState = new FlowPaginationTableState( paginationTableState );
        paginationTableState.suspendVisualStateCollection( false );
      }
    }

    // ignore all other break requests ..
    return startBlockLevelBox( box );
  }

  protected void finishTableSectionLevelBox( final RenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
      if ( box.isOpen() ) {
        paginationTableState = paginationTableState.pop();
      }
    }
    finishBlockLevelBox( box );
  }

  protected boolean startTableRowLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    return startRowLevelBox( box );
  }

  protected void finishTableRowLevelBox( final RenderBox box ) {
    finishRowLevelBox( box );
  }

  protected boolean startTableCellLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    installTableContext( box );
    return startBlockLevelBox( box );
  }

  protected void finishTableCellLevelBox( final RenderBox box ) {
    finishBlockLevelBox( box );
    uninstallTableContext( box );
  }

  protected boolean startInlineLevelBox( final RenderBox box ) {
    box.setOverflowAreaHeight( box.getCachedHeight() );

    BoxShifter.shiftBox( box, shiftState.getShiftForNextChild() );
    return false;
  }

  protected void processInlineLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void finishInlineLevelBox( final RenderBox box ) {
  }

  protected boolean startTableColLevelBox( final RenderBox box ) {
    return false;
  }

  protected boolean startTableColGroupLevelBox( final RenderBox box ) {
    return false;
  }

  protected void processCanvasLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processRowLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processOtherLevelChild( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processTableLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processTableRowLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processTableSectionLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processTableCellLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  protected void processTableColLevelNode( final RenderNode node ) {
    node.setY( node.getY() + shiftState.getShiftForNextChild() );
  }

  private void updateStateKey( final RenderBox box ) {
    if ( paginationTableState.isVisualStateCollectionSuspended() ) {
      return;
    }

    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return;
    }

    final long y = box.getY();
    if ( recordedPageBreakPosition != 0 && y >= recordedPageBreakPosition ) {
      return;
    }

    final ReportStateKey stateKey = box.getStateKey();
    if ( stateKey != null && stateKey.isInlineSubReportState() == false ) {
      this.visualState = stateKey;
    }
  }

  private void updateStateKeyDeep( final RenderBox box ) {
    if ( paginationTableState.isVisualStateCollectionSuspended() ) {
      return;
    }

    final long y = box.getY();
    if ( recordedPageBreakPosition != 0 && y >= recordedPageBreakPosition ) {
      return;
    }

    final ReportStateKey reportStateKey = findOldestProcessKeyStep.find( box );
    if ( reportStateKey != null && reportStateKey.isInlineSubReportState() == false ) {
      this.visualState = reportStateKey;
    }
  }

  private boolean handleAutomaticPagebreak( final RenderBox box, final PaginationShiftState boxContext ) {
    final long shift = boxContext.getShiftForNextChild();
    final PageBreakPositions breakUtility = paginationTableState.getBreakPositions();
    final long boxHeightAndWidowArea =
        Math.max( box.getHeight(), PaginationStepLib.getWidowConstraint( box, shiftState, paginationTableState ) );
    if ( breakUtility.isCrossingPagebreak( box.getY(), boxHeightAndWidowArea, shift ) == false ) {
      // The whole box fits on the current page. No need to do anything fancy.
      final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
      if ( breakIndicator == RenderBox.BreakIndicator.INDIRECT_MANUAL_BREAK ) {
        // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
        // for now, we will only apply the ordinary shift.
        final long boxY = box.getY();
        box.setY( boxY + shift );
        updateStateKey( box );
        return true;
      } else { // if (breakIndicator == RenderBox.BreakIndicator.NO_MANUAL_BREAK)
        // As neither this box nor any of the children will cause a pagebreak, we can shift them and skip the processing
        // from here.
        BoxShifter.shiftBox( box, shift );
        updateStateKeyDeep( box );
        return false;
      }
    }

    // At this point we know, that the box may cause some shifting. It crosses at least one minor or major pagebreak.
    // Right now, we are just evaluating the next break. In a future version, we could search all possible break
    // positions up to the next major break.
    final long boxY = box.getY();
    final long boxYShifted = boxY + shift;
    final long nextMinorBreak = breakUtility.findNextBreakPosition( boxYShifted );
    final long spaceAvailable = nextMinorBreak - boxYShifted;

    // This box sits directly on a pagebreak. This means, the page is empty, and there is no need for additional
    // shifting.
    if ( spaceAvailable == 0 ) {
      box.setY( boxYShifted );
      updateStateKey( box );
      if ( boxYShifted < nextMinorBreak ) {
        // this position is shifted, but not header-shifted
        box.markPinned( nextMinorBreak );
      }
      return true;
    }

    final long spaceConsumed = PaginationStepLib.computeNonBreakableBoxHeight( box, boxContext, paginationTableState );
    if ( spaceAvailable < spaceConsumed ) {
      // So we have not enough space to fulfill the layout-constraints. Be it so. Lets shift the box to the next
      // break.
      // check whether we can actually shift the box. We will have to take the previous widow/orphan operations
      // into account.
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Automatic pagebreak, after orphan-opt-out: " + box );
        logger.debug( "Automatic pagebreak                      : " + visualState );
      }
      final long nextShift = nextMinorBreak - boxY;
      box.setY( boxY + nextShift );
      boxContext.setShift( nextShift );
      updateStateKey( box );
      if ( box.getY() < nextMinorBreak ) {
        box.markPinned( nextMinorBreak );
      }
      return true;
    }

    // OK, there *is* enough space available. Start the normal processing
    box.setY( boxYShifted );
    updateStateKey( box );
    return true;
  }

  private boolean handleManualBreakOnBox( final RenderBox box, final PaginationShiftState boxContext,
      final boolean breakPending ) {
    final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
    // First check the simple cases:
    // If the box wants to break, then there's no point in waiting: Shift the box and continue.
    if ( breakIndicator != RenderBox.BreakIndicator.DIRECT_MANUAL_BREAK && breakPending == false ) {
      return false;
    }

    final PageBreakPositions breakUtility = paginationTableState.getBreakPositions();
    final long shift = boxContext.getShiftForNextChild();
    final long boxY = box.getY();
    final long shiftedBoxY = boxY + shift;
    final long nextMajorBreak = breakUtility.findNextMajorBreakPosition( shiftedBoxY );
    if ( nextMajorBreak < shiftedBoxY ) {
      // This band will be outside the last pagebreak. We can only shift it normally, but there is no way
      // that we could shift it to the final position yet.
      box.setY( shiftedBoxY );
    } else if ( paginationTableState.isTableProcessing() == false || shiftedBoxY > nextMajorBreak ) {
      final long nextShift = nextMajorBreak - boxY;
      box.setY( boxY + nextShift );
      boxContext.setShift( nextShift );
    } else {
      box.setY( shiftedBoxY );
    }

    final long pageEnd = paginationTableState.getPageOffset();
    if ( box.getY() <= pageEnd ) {
      updateStateKey( box );
      box.markPinned( pageEnd );
    } else if ( recordedPageBreakPosition == 0 ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Breaking on box " + box );
      }
      recordedPageBreakPosition = box.getY();
    }

    if ( recordedPageBreakPosition == box.getY() && box.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK ) {
      recordedPageBreakPositionIsForced = true;
    }
    return true;
  }

  protected void installTableContext( final RenderBox box ) {
    if ( box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return;
    }
    paginationTableState = new FlowPaginationTableState( paginationTableState );
  }

  protected void uninstallTableContext( final RenderBox box ) {
    if ( box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return;
    }
    paginationTableState = paginationTableState.pop();
  }
}
