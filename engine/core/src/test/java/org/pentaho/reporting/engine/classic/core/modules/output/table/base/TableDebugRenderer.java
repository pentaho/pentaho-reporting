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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.layout.AbstractRenderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.IterativeOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyAutoCommitPageHeaderStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CleanPaginatedBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FillFlowPagesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FlowPaginationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;

/**
 * This class exists only for driving the table-validation testcases. It is not a valid renderer and exhibits major
 * flaws if used elsewhere. You better do not use this outside the test-environment or evil aliens will kidnap you an
 * explain you in very detailed words what "Stay away from this class" really means.
 *
 * @author Thomas Morgner
 */
public class TableDebugRenderer extends AbstractRenderer {
  private FlowPaginationStep paginationStep;
  private FillFlowPagesStep fillPhysicalPagesStep;
  private CleanPaginatedBoxesStep cleanPaginatedBoxesStep;
  private ApplyAutoCommitPageHeaderStep applyAutoCommitPageHeaderStep;
  private int flowCount;
  private boolean pageStartPending;

  public TableDebugRenderer( final OutputProcessor outputProcessor ) {
    super( outputProcessor );
    this.paginationStep = new FlowPaginationStep();
    this.fillPhysicalPagesStep = new FillFlowPagesStep();
    this.cleanPaginatedBoxesStep = new CleanPaginatedBoxesStep();
    this.applyAutoCommitPageHeaderStep = new ApplyAutoCommitPageHeaderStep();

    initialize();
  }

  public int getPageCount() {
    return 0;
  }

  protected boolean isPageFinished() {
    final LogicalPageBox pageBox = getPageBox();
    final PaginationResult pageBreak = paginationStep.performPagebreak( pageBox );
    if ( pageBreak.isOverflow() || pageBox.isOpen() == false ) {
      setLastStateKey( pageBreak.getLastVisibleState() );
      return true;
    }
    return false;
  }

  public void processIncrementalUpdate( final boolean performOutput ) throws ContentProcessingException {
    if ( isDirty() == false ) {
      return;
    }
    clearDirty();

    final OutputProcessor outputProcessor = getOutputProcessor();
    if ( outputProcessor instanceof IterativeOutputProcessor == false
        || outputProcessor.getMetaData().isFeatureSupported( OutputProcessorFeature.ITERATIVE_RENDERING ) == false ) {
      return;
    }

    final LogicalPageBox pageBox = getPageBox();
    pageBox.setPageEnd( pageBox.getHeight() );

    if ( pageBox.isOpen() ) {
      final IterativeOutputProcessor io = (IterativeOutputProcessor) outputProcessor;
      if ( applyAutoCommitPageHeaderStep.compute( pageBox ) ) {
        io.processIterativeContent( pageBox, performOutput );
      }
    }
  }

  protected boolean
    performPagination( final LayoutPagebreakHandler layoutPagebreakHandler, final boolean performOutput )
      throws ContentProcessingException {
    final OutputProcessor outputProcessor = getOutputProcessor();
    // next: perform pagination.
    final LogicalPageBox pageBox = getPageBox();
    final PaginationResult pageBreak = paginationStep.performPagebreak( pageBox );
    if ( pageBreak.isOverflow() || pageBox.isOpen() == false ) {
      setLastStateKey( pageBreak.getLastVisibleState() );
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
          outputProcessor.processContent( box );
        } else {
          outputProcessor.processContent( pageBox );
        }
      } else {
        outputProcessor.processRecomputedContent( pageBox );
      }

      // Now fire the pagebreak. This goes through all layers and informs all
      // components, that a pagebreak has been encountered and possibly a
      // new page has been set. It does not save the state or perform other
      // expensive operations. However, it updates the 'isPagebreakEncountered'
      // flag, which will be active until the input-feed received a new event.
      final boolean repeat = pageBox.isOpen() || pageBreak.isOverflow();
      if ( repeat ) {
        // pageBox.setAllVerticalBreaks(pageBreak.getAllBreaks());
        // First clean all boxes that have been marked as finished. This reduces the overall complexity of the
        // pagebox and improves performance on huge reports.

        cleanPaginatedBoxesStep.compute( pageBox );

        pageBox.setPageOffset( nextOffset );
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
        return false;
      }
    } else if ( outputProcessor instanceof IterativeOutputProcessor
        && outputProcessor.getMetaData().isFeatureSupported( OutputProcessorFeature.ITERATIVE_RENDERING ) ) {
      processIncrementalUpdate( performOutput );
    }
    return false;
  }

  public int getFlowCount() {
    return flowCount;
  }

  public boolean isCurrentPageEmpty() {
    // todo: Invent a test that checks whether the page is currently empty.
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
}
