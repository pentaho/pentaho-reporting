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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.BreakMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositions;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.crosstab.CrosstabOutputHelper;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.util.InitialPaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationShiftState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationShiftStatePool;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationTableState;
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
@SuppressWarnings( "HardCodedStringLiteral" )
public final class PaginationStep extends IterateVisualProcessStep {
  private static final Log logger = LogFactory.getLog( PaginationStep.class );
  private boolean breakPending;
  private FindOldestProcessKeyStep findOldestProcessKeyStep;
  private PageBreakPositionList basePageBreakList;
  private ReportStateKey visualState;
  private BreakMarkerRenderBox breakIndicatorEncountered;
  private PaginationTableState paginationTableState;
  private PaginationShiftState shiftState;
  private PaginationShiftStatePool shiftStatePool;
  private long pageOffsetKey;
  private long usablePageHeight;

  public PaginationStep() {
    findOldestProcessKeyStep = new FindOldestProcessKeyStep();
    basePageBreakList = new PageBreakPositionList();
    shiftStatePool = new PaginationShiftStatePool();
  }

  public PaginationResult performPagebreak( final LogicalPageBox pageBox ) {
    getEventWatch().start();
    getSummaryWatch().start();
    try {
      PaginationStepLib.assertProgress( pageBox );

      if ( logger.isDebugEnabled() ) {
        logger.debug( "Start pagination ... " + pageBox.getPageOffset() );
      }
      this.breakIndicatorEncountered = null;
      this.visualState = null;
      this.pageOffsetKey = pageBox.getPageOffset();
      this.shiftState = new InitialPaginationShiftState();
      this.breakPending = false;
      this.usablePageHeight = Long.MAX_VALUE;

      final long[] allCurrentBreaks = pageBox.getPhysicalBreaks( RenderNode.VERTICAL_AXIS );
      if ( allCurrentBreaks.length == 0 ) {
        // No maximum height.
        throw new InvalidReportStateException( "No page given. This is really bad." );
      }

      // Note: For now, we limit both the header and footer to a single physical
      // page. This safes me a lot of trouble for now.
      final long lastBreakLocal = allCurrentBreaks[allCurrentBreaks.length - 1];
      final long reservedHeight = PaginationStepLib.restrictPageAreaHeights( pageBox, allCurrentBreaks );
      if ( reservedHeight >= lastBreakLocal ) {
        // This is also bad. There will be no space left to print a single element.
        throw new InvalidReportStateException(
            "Header and footer consume the whole page. No space left for normal-flow." );
      }

      PaginationStepLib.configureBreakUtility( basePageBreakList, pageBox, allCurrentBreaks, reservedHeight,
          lastBreakLocal );

      final long pageEnd = basePageBreakList.getLastMasterBreak();
      final long pageHeight = pageBox.getPageHeight();
      this.paginationTableState =
          new PaginationTableState( pageHeight, pageBox.getPageOffset(), pageEnd, basePageBreakList );

      // now process all the other content (excluding the header and footer area)
      if ( startBlockLevelBox( pageBox ) ) {
        final TableSectionRenderBox tableHeader = findTableHeader( pageBox );
        if ( tableHeader != null ) {
          if ( tableHeader.getHeight() >= lastBreakLocal ) {
            throw new InvalidReportStateException( "Table header consume the whole page. No space left for normal-flow." );
          }
        }
        processBoxChilds( pageBox );
      }
      finishBlockLevelBox( pageBox );

      PaginationStepLib.assertProgress( pageBox );

      final long usedPageHeight = Math.min( pageBox.getHeight(), usablePageHeight );
      final long masterBreak = basePageBreakList.getLastMasterBreak();
      final boolean overflow;
      if ( breakIndicatorEncountered != null ) {
        if ( breakIndicatorEncountered.getY() <= pageBox.getPageOffset() ) {
          overflow = usedPageHeight > masterBreak;
        } else {
          overflow = true;
        }
      } else {
        overflow = usedPageHeight > masterBreak;
      }
      final boolean nextPageContainsContent = ( pageBox.getHeight() > masterBreak );
      return new PaginationResult( basePageBreakList, overflow, nextPageContainsContent, visualState );
    } finally {
      this.breakIndicatorEncountered = null;
      this.paginationTableState = null;
      this.visualState = null;
      this.shiftState = null;
      getEventWatch().stop();
      getSummaryWatch().stop( true );
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

    PaginationStepLib.assertBlockPosition( box, shift );
    handleBlockLevelBoxFinishedMarker( box, shift );

    if ( shiftState.isManualBreakSuspended() == false ) {
      if ( handleManualBreakOnBox( box, shiftState, breakPending ) ) {
        breakPending = false;
        return true;
      }
      breakPending = false;
    }

    // If this box does not cross any (major or minor) break, it may need no additional shifting at all.
    final RenderLength fixedPositionLength = box.getBoxDefinition().getFixedPosition();
    if ( shiftState.isManualBreakSuspended() || RenderLength.AUTO.equals( fixedPositionLength )
        || paginationTableState.isFixedPositionProcessingSuspended() ) {
      return handleAutomaticPagebreak( box, shiftState );
    }

    // If you've come this far, this means, that your box has a fixed position defined.
    final long boxY = box.getY();
    final long shiftedBoxPosition = boxY + shift;
    final long fixedPositionResolved = fixedPositionLength.resolve( paginationTableState.getPageHeight(), 0 );
    final PageBreakPositions breakUtility = paginationTableState.getBreakPositions();
    final long fixedPositionInFlow =
        breakUtility.computeFixedPositionInFlow( shiftedBoxPosition, fixedPositionResolved );
    if ( fixedPositionInFlow < shiftedBoxPosition ) {
      // ... but the fixed position is invalid, so treat it as non-defined.
      return handleAutomaticPagebreak( box, shiftState );
    }

    // The computed break seems to be valid.
    // Compute what happens if the whole box can fit on the current page.
    // We have an opportunity to optimize our processing by skipping all content if there are no
    // manual pagebreaks defined on one of the childs.
    if ( breakUtility.isCrossingPagebreakWithFixedPosition( shiftedBoxPosition, box.getHeight(), fixedPositionResolved ) == false ) {
      return handleFixedPositionWithoutBreakOnBox( box, shift, fixedPositionInFlow );
    }

    // The box will not fit on the current page.
    //
    // A box with a fixed position will always be printed at this position, even if it does not seem
    // to fit there. If we move the box, we would break the explicit layout constraint 'fixed-position' in
    // favour of an implicit one ('page-break: avoid').

    // Treat as if there is enough space available. Start the normal processing.
    final long fixedPositionDelta = fixedPositionInFlow - shiftedBoxPosition;
    shiftState.setShift( shift + fixedPositionDelta );
    box.setY( fixedPositionInFlow );
    updateStateKey( box );
    return true;
  }

  private boolean handleFixedPositionWithoutBreakOnBox( final RenderBox box, final long shift,
      final long fixedPositionInFlow ) {
    final long boxY = box.getY();
    final long shiftedBoxPosition = boxY + shift;
    final long fixedPositionDelta = fixedPositionInFlow - shiftedBoxPosition;
    final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
    if ( breakIndicator == RenderBox.BreakIndicator.INDIRECT_MANUAL_BREAK ) {
      // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
      // for now, we will only apply the ordinary shift.
      box.setY( fixedPositionInFlow );
      shiftState.setShift( shift + fixedPositionDelta );
      updateStateKey( box );
      return true;
    } else { // if (breakIndicator == RenderBox.BreakIndicator.NO_MANUAL_BREAK)
      // The whole box fits on the current page. However, we have to apply the shifting to move the box
      // to its defined fixed-position.
      //
      // As neither this box nor any of the children will cause a pagebreak, we can shift them and skip the processing
      // from here.
      BoxShifter.shiftBox( box, fixedPositionDelta );
      updateStateKeyDeep( box );
      return false;
    }
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

    if ( box.isCommited() ) {
      box.setFinishedPaginate( true );
    }

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

    if ( box.isCommited() ) {
      box.setFinishedPaginate( true );
    }

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

          paginationTableState = new PaginationTableState( paginationTableState );
          paginationTableState.suspendVisualStateCollection( true );

          startTableHeaderSection( box, sectionRenderBox );
          return false;
        }
        case FOOTER: {
          shiftState = shiftStatePool.create( box, shiftState );

          paginationTableState = new PaginationTableState( paginationTableState );
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
      shiftState = shiftStatePool.create( box, shiftState );

      return true;
    }
  }

  private RenderBox findRootBox( RenderBox box ) {
    RenderBox parent = box.getParent();
    while ( parent != null ) {
      if ( parent.isContainsReservedContent() == false ) {
        return box;
      }
      box = parent;
      parent = box.getParent();
    }
    return box;
  }

  private void startTableHeaderSection( final RenderBox _box, final TableSectionRenderBox sectionRenderBox ) {
    RenderBox box = findRootBox( _box );

    final long contextShift = shiftState.getShiftForNextChild();
    // shift the header downwards,
    // 1. Check that this table actually breaks across the current page. Header position must be
    // before the pagebox-offset. If not, return false, after the normal shifting.
    final long pageOffset = paginationTableState.getPageOffset();
    final long delta = pageOffset - ( sectionRenderBox.getY() + contextShift );
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
        paginationTableState = new PaginationTableState( paginationTableState );
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
    box.setOverflowAreaHeight( box.getHeight() );

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
    if ( y < paginationTableState.getPageEnd() ) {
      final ReportStateKey stateKey = box.getStateKey();
      if ( stateKey != null && stateKey.isInlineSubReportState() == false ) {
        this.visualState = stateKey;
      }
    }
  }

  private void updateStateKeyDeep( final RenderBox box ) {
    if ( paginationTableState.isVisualStateCollectionSuspended() ) {
      return;
    }

    final long y = box.getY();
    if ( y >= paginationTableState.getPageEnd() ) {
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
        Math.max( box.getHeight(), PaginationStepLib.getWidowConstraint( box, boxContext, paginationTableState ) );
    if ( breakUtility.isCrossingPagebreak( box.getY(), boxHeightAndWidowArea, shift ) == false ) {
      // The whole box fits on the current page. No need to do anything fancy.
      final RenderBox.BreakIndicator breakIndicator = box.getManualBreakIndicator();
      if ( breakIndicator == RenderBox.BreakIndicator.INDIRECT_MANUAL_BREAK
          || box.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.RESTRICTED ) {
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
      final long nextShift = nextMinorBreak - boxY;
      final long shiftDelta = nextShift - shift;
      if ( shiftDelta > 0 ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Automatic pagebreak, after orphan-opt-out: " + box );
          logger.debug( "Automatic pagebreak                      : " + visualState );
        }
      }
      box.setY( boxY + nextShift );
      boxContext.setShift( nextShift );
      updateStateKey( box );
      box.markPinned( nextMinorBreak );
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

    final long shift = boxContext.getShiftForNextChild();
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK ) {
      final BreakMarkerRenderBox bmrb = (BreakMarkerRenderBox) box;
      final long pageOffsetForMarker = bmrb.getValidityRange();
      final long pageEndForOffset =
          paginationTableState.getBreakPositions().findPageEndForPageStartPosition( pageOffsetForMarker );
      if ( ( box.getY() + shift ) > pageEndForOffset ) {
        // we ignore this one. It has been pushed outside of the page for which it was generated.
        return false;
      }

      if ( this.breakIndicatorEncountered == null || this.breakIndicatorEncountered.getY() < ( bmrb.getY() + shift ) ) {
        this.breakIndicatorEncountered = bmrb;
      }
    }

    final PageBreakPositions breakUtility = paginationTableState.getBreakPositions();
    final RenderLength fixedPosition = box.getBoxDefinition().getFixedPosition();
    final long fixedPositionResolved = fixedPosition.resolve( paginationTableState.getPageHeight(), 0 );
    final long boxY = box.getY();
    final long shiftedBoxY = boxY + shift;
    final long nextNonShiftedMajorBreak = breakUtility.findNextMajorBreakPosition( shiftedBoxY );
    final long fixedPositionOnNextPage =
        breakUtility.computeFixedPositionInFlow( nextNonShiftedMajorBreak, fixedPositionResolved );
    final long nextMajorBreak = Math.max( nextNonShiftedMajorBreak, fixedPositionOnNextPage );
    if ( nextMajorBreak < shiftedBoxY ) {
      // This band will be outside the last pagebreak. We can only shift it normally, but there is no way
      // that we could shift it to the final position yet.
      box.setY( boxY + shift );
    } else {
      final long nextShift = nextMajorBreak - boxY;
      final long shiftDelta = nextShift - shift;
      box.setY( boxY + nextShift );
      boxContext.setShift( nextShift );
    }

    updateStateKey( box );
    final long pageEnd = paginationTableState.getPageEnd();
    if ( box.getY() < pageEnd ) {
      box.markPinned( pageEnd );
    }
    return true;
  }

  private void handleBlockLevelBoxFinishedMarker( final RenderBox box, final long shift ) {
    if ( box.isFinishedPaginate() ) {
      // if already marked as finished, no need to do more work ..
      return;
    }

    if ( box.isCommited() ) {
      box.setFinishedPaginate( true );
    } else {
      final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
      if ( sblp.isAvoidPagebreakInside() || sblp.getWidows() > 0 || sblp.getOrphans() > 0 ) {
        // Check, whether this box sits on a break-position. In that case, we can call that box finished as well.
        final long boxY = box.getY();
        final PageBreakPositions breakUtility = paginationTableState.getBreakPositions();
        final long nextMinorBreak = breakUtility.findNextBreakPosition( boxY + shift );
        final long spaceAvailable = nextMinorBreak - ( boxY + shift );

        // This box sits directly on a pagebreak. No matter how much content we fill in the box, it will not move.
        // This makes this box a finished box.
        if ( spaceAvailable == 0 || box.isPinned() ) {
          box.setFinishedPaginate( true );
        }
      } else {
        // This box defines no constraints that would cause a shift of it later in the process. We can treat it as
        // if it is finished already ..
        box.setFinishedPaginate( true );
      }
    }
  }

  protected void installTableContext( final RenderBox box ) {
    if ( box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return;
    }
    paginationTableState = new PaginationTableState( paginationTableState );
  }

  protected void uninstallTableContext( final RenderBox box ) {
    if ( box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return;
    }
    paginationTableState = paginationTableState.pop();
  }


  private TableSectionRenderBox findTableHeader( final LogicalPageBox pageBox ) {
    RenderNode node = pageBox.getFirstChild();
    RenderBox tableBox = null;
    while ( node != null ) {
      if ( node instanceof RenderBox ) {
        if ( node.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
          tableBox = (RenderBox) node;
          break;
        } else {
          node = ( (RenderBox) node ).getFirstChild();
        }
      } else {
        node = node.getNext();
      }
    }
    return CrosstabOutputHelper.getTableSectionRenderBox( tableBox );
  }
}
