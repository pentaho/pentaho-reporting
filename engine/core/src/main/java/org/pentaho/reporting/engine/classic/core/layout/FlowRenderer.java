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

package org.pentaho.reporting.engine.classic.core.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.IterativeOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyAutoCommitPageHeaderStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CleanFlowBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CountBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FillFlowPagesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FlowPaginationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;

/**
 * A flow renderer is a light-weight paginating renderer. It does not care about the page-size but searches for manual
 * breaks. Once a manual break is encountered, the flow shifts and creates a page-event. (This is the behavior of the
 * old table-exporters.)
 * <p/>
 * This implementation is a mix of a paginated and streaming renderer.
 *
 * @author Thomas Morgner
 */
public class FlowRenderer extends AbstractRenderer {
  private static Log logger = LogFactory.getLog( FlowRenderer.class );

  private FlowPaginationStep paginationStep;
  private FillFlowPagesStep fillPhysicalPagesStep;
  private CleanFlowBoxesStep cleanFlowBoxesStep;
  private ApplyAutoCommitPageHeaderStep applyAutoCommitPageHeaderStep;
  private int flowCount;
  private boolean pageStartPending;
  private int floodPrevention;
  private CountBoxesStep countBoxesStep;

  public FlowRenderer( final OutputProcessor outputProcessor ) {
    super( outputProcessor );
    this.paginationStep = new FlowPaginationStep();
    this.fillPhysicalPagesStep = new FillFlowPagesStep();
    this.cleanFlowBoxesStep = new CleanFlowBoxesStep();
    this.applyAutoCommitPageHeaderStep = new ApplyAutoCommitPageHeaderStep();
    this.countBoxesStep = new CountBoxesStep();

    initialize();
  }

  protected boolean isPageFinished() {
    final LogicalPageBox pageBox = getPageBox();
    // final long sizeBeforePagination = pageBox.getHeight();
    // final LogicalPageBox clone = (LogicalPageBox) pageBox.deriveForAdvance(true);
    final PaginationResult pageBreak = paginationStep.performPagebreak( pageBox );
    if ( pageBreak.isOverflow() || pageBox.isOpen() == false ) {
      setLastStateKey( pageBreak.getLastVisibleState() );
      return true;
    }
    return false;
  }

  public void startReport( final ReportDefinition report, final ProcessingContext processingContext,
      final PerformanceMonitorContext performanceMonitorContext ) {
    flowCount = 0;
    super.startReport( report, processingContext, performanceMonitorContext );
  }

  protected void debugPrint( final LogicalPageBox pageBox ) {
  }

  public void processIncrementalUpdate( final boolean performOutput ) throws ContentProcessingException {
    if ( isDirty() == false ) {
      logger.debug( "Not dirty, no update needed." );
      return;
    }
    clearDirty();

    floodPrevention += 1;
    if ( floodPrevention < 5 ) { // this is a magic number ..
      return;
    }
    floodPrevention = 0;

    final OutputProcessor outputProcessor = getOutputProcessor();
    if ( outputProcessor instanceof IterativeOutputProcessor == false
        || outputProcessor.getMetaData().isFeatureSupported( OutputProcessorFeature.ITERATIVE_RENDERING ) == false ) {
      logger.debug( "No incremental system." );
      return;
    }

    final LogicalPageBox pageBox = getPageBox();
    pageBox.setPageEnd( pageBox.getHeight() );

    if ( pageBox.isOpen() ) {
      final IterativeOutputProcessor io = (IterativeOutputProcessor) outputProcessor;
      if ( applyAutoCommitPageHeaderStep.compute( pageBox ) ) {
        io.processIterativeContent( pageBox, performOutput );
        countBoxesStep.process( pageBox );
        cleanFlowBoxesStep.compute( pageBox );

        // ModelPrinter.INSTANCE.print(pageBox);

        logger.debug( "Computing Incremental update: offset=" + pageBox.getPageOffset() + ", horizon="
            + pageBox.getProcessedTableOffset() + ", pageEnd=" + pageBox.getPageEnd() );

      }
    }
  }

  protected boolean
    performPagination( final LayoutPagebreakHandler layoutPagebreakHandler, final boolean performOutput )
      throws ContentProcessingException {
    final OutputProcessor outputProcessor = getOutputProcessor();
    // next: perform pagination.
    final LogicalPageBox pageBox = getPageBox();
    // final long sizeBeforePagination = pageBox.getHeight();
    // final LogicalPageBox clone = (LogicalPageBox) pageBox.deriveForAdvance(true);
    final PaginationResult pageBreak = paginationStep.performPagebreak( pageBox );
    if ( pageBreak.isOverflow() == false && pageBox.isOpen() ) {
      return false;
    }

    setLastStateKey( pageBreak.getLastVisibleState() );
    // final long sizeAfterPagination = pageBox.getHeight();
    setPagebreaks( getPagebreaks() + 1 );
    pageBox.setAllVerticalBreaks( pageBreak.getAllBreaks() );

    flowCount += 1;
    debugPrint( pageBox );

    // A new page has been started. Recover the page-grid, then restart
    // everything from scratch. (We have to recompute, as the pages may
    // be different now, due to changed margins or page definitions)
    final long nextOffset = pageBox.computePageEnd();
    pageBox.setPageEnd( nextOffset );
    final long pageOffset = pageBox.getPageOffset();

    if ( performOutput ) {
      if ( outputProcessor.isNeedAlignedPage() ) {
        final LogicalPageBox box = fillPhysicalPagesStep.compute( pageBox, pageOffset, nextOffset );
        logger
            .debug( "Processing contents for Page " + flowCount + " Page-Offset: " + pageOffset + " -> " + nextOffset );

        outputProcessor.processContent( box );
      } else {
        logger.debug( "Processing fast contents for Page " + flowCount + " Page-Offset: " + pageOffset + " -> "
            + nextOffset );
        outputProcessor.processContent( pageBox );
      }
    } else {
      logger.debug( "Recomputing contents for Page " + flowCount + " Page-Offset: " + pageOffset + " -> " + nextOffset );
      outputProcessor.processRecomputedContent( pageBox );
    }

    // Now fire the pagebreak. This goes through all layers and informs all
    // components, that a pagebreak has been encountered and possibly a
    // new page has been set. It does not save the state or perform other
    // expensive operations. However, it updates the 'isPagebreakEncountered'
    // flag, which will be active until the input-feed received a new event.
    // Log.debug("PageTime " + (currentPageAge - lastPageAge));

    final boolean repeat = pageBox.isOpen() || pageBreak.isOverflow();
    if ( repeat ) {
      // pageBox.setAllVerticalBreaks(pageBreak.getAllBreaks());
      // First clean all boxes that have been marked as finished. This reduces the overall complexity of the
      // pagebox and improves performance on huge reports.
      countBoxesStep.process( pageBox );
      cleanFlowBoxesStep.compute( pageBox );

      // cleanPaginatedBoxesStep.compute(pageBox);
      pageBox.setPageOffset( nextOffset );
      // todo PRD-4606
      pageBox.resetCacheState( true );

      if ( pageBreak.isNextPageContainsContent() ) {
        if ( layoutPagebreakHandler != null ) {
          layoutPagebreakHandler.pageStarted();
        }
        return true;
      }
      // No need to try again, we know that the result will not change, as the next page is
      // empty. (We already tested it.)
      pageStartPending = true;
      return false;
    } else {
      outputProcessor.processingFinished();
      pageBox.setPageOffset( nextOffset );
      // todo PRD-4606
      // pageBox.resetCacheState(true);
      return false;
    }
  }

  public int getPageCount() {
    return flowCount;
  }

  public boolean isCurrentPageEmpty() {
    final LogicalPageBox logicalPageBox = getPageBox();
    final PageBreakPositionList breakPositionList = logicalPageBox.getAllVerticalBreaks();
    final long masterBreak = breakPositionList.getLastMasterBreak();
    final boolean nextPageContainsContent = ( logicalPageBox.getHeight() > masterBreak );
    return nextPageContainsContent == false;
  }

  public boolean clearPendingPageStart( final LayoutPagebreakHandler layoutPagebreakHandler ) {
    if ( pageStartPending == false ) {
      return false;
    }

    if ( layoutPagebreakHandler != null ) {
      layoutPagebreakHandler.pageStarted();
    }
    pageStartPending = false;
    return true;
  }

  public boolean isPageStartPending() {
    return pageStartPending;
  }

  protected void initializeRendererOnStartReport( final ProcessingContext processingContext ) {
    super.initializeRendererOnStartReport( processingContext );
    paginationStep.initializePerformanceMonitoring( getPerformanceMonitorContext() );
    fillPhysicalPagesStep.initializePerformanceMonitoring( getPerformanceMonitorContext() );
  }

  protected void close() {
    super.close();
    paginationStep.close();
    fillPhysicalPagesStep.close();
  }
}
