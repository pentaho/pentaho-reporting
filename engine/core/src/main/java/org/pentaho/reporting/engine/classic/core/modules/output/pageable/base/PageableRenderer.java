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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.AbstractRenderer;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.CleanPaginatedBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CountBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.FillPhysicalPagesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.OrphanStep;
import org.pentaho.reporting.engine.classic.core.layout.process.PaginationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.WidowStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationResult;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;

@SuppressWarnings( "HardCodedStringLiteral" )
public class PageableRenderer extends AbstractRenderer {
  private static final Log logger = LogFactory.getLog( PageableRenderer.class );
  private PaginationStep paginationStep;
  private OrphanStep orphanStep;
  private WidowStep widowStep;
  private FillPhysicalPagesStep fillPhysicalPagesStep;
  private CleanPaginatedBoxesStep cleanPaginatedBoxesStep;
  private int pageCount;
  private boolean pageStartPending;
  private CountBoxesStep countBoxesStep;
  private boolean widowsEnabled;

  public PageableRenderer( final OutputProcessor outputProcessor ) {
    super( outputProcessor );
    this.paginationStep = new PaginationStep();
    this.fillPhysicalPagesStep = new FillPhysicalPagesStep();
    this.cleanPaginatedBoxesStep = new CleanPaginatedBoxesStep();
    this.countBoxesStep = new CountBoxesStep();
    this.orphanStep = new OrphanStep();
    this.widowStep = new WidowStep();
    initialize();
  }

  public void startReport( final ReportDefinition report, final ProcessingContext processingContext,
      final PerformanceMonitorContext performanceMonitorContext ) {
    super.startReport( report, processingContext, performanceMonitorContext );
    pageCount = 0;
    widowsEnabled = !ClassicEngineBoot.isEnforceCompatibilityFor( processingContext.getCompatibilityLevel(), 3, 8 );
  }

  protected void debugPrint( final LogicalPageBox pageBox ) {
    // printConditional(5, pageBox);
    // printConditional(18, pageBox);
  }

  protected void printConditional( final int page, final LogicalPageBox pageBox ) {
    if ( logger.isDebugEnabled() == false ) {
      return;
    }

    logger.debug( "Printing a page: " + pageCount );
    if ( pageCount == page ) {
      // leave the debug-code in until all of these cases are solved.
      logger.debug( "1: **** Start Printing Page: " + pageCount );
      // ModelPrinter.INSTANCE.print(clone);
      ModelPrinter.INSTANCE.print( pageBox );
      logger.debug( "1: **** Stop  Printing Page: " + pageCount );
    }

  }

  protected boolean preparePagination( final LogicalPageBox pageBox ) {
    if ( widowsEnabled == false ) {
      return true;
    }
    if ( isWidowOrphanDefinitionsEncountered() == false ) {
      return true;
    }

    if ( orphanStep.processOrphanAnnotation( pageBox ) ) {
      // logger.info("Orphans unlayoutable.");
      return false;
    }
    if ( widowStep.processWidowAnnotation( pageBox ) ) {
      // logger.info("Widows unlayoutable.");
      return false;
    }
    return true;
  }

  protected boolean isPageFinished() {
    final LogicalPageBox pageBox = getPageBox();
    // final long sizeBeforePagination = pageBox.getHeight();
    // final LogicalPageBox clone = (LogicalPageBox) pageBox.derive(true);
    final PaginationResult pageBreak = paginationStep.performPagebreak( pageBox );
    if ( pageBreak.isOverflow() || pageBox.isOpen() == false ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Detected pagebreak : " + pageBreak.getLastVisibleState() );
      }
      setLastStateKey( pageBreak.getLastVisibleState() );
      return true;
    }
    return false;
  }

  protected boolean
    performPagination( final LayoutPagebreakHandler layoutPagebreakHandler, final boolean performOutput )
      throws ContentProcessingException {
    // next: perform pagination.
    final LogicalPageBox pageBox = getPageBox();

    // final long sizeBeforePagination = pageBox.getHeight();
    // final LogicalPageBox clone = (LogicalPageBox) pageBox.derive(true);
    final PaginationResult pageBreak = paginationStep.performPagebreak( pageBox );
    if ( pageBox.isOpen() && pageBreak.isOverflow() == false ) {
      return false;
    }

    setLastStateKey( pageBreak.getLastVisibleState() );
    setPagebreaks( getPagebreaks() + 1 );
    pageBox.setAllVerticalBreaks( pageBreak.getAllBreaks() );

    pageCount += 1;

    // DebugLog.log("1: **** Start Printing Page: " + pageCount);
    debugPrint( pageBox );
    // DebugLog.log("PaginationResult: " + pageBreak);

    // A new page has been started. Recover the page-grid, then restart
    // everything from scratch. (We have to recompute, as the pages may
    // be different now, due to changed margins or page definitions)
    final OutputProcessor outputProcessor = getOutputProcessor();
    final long nextOffset = pageBreak.getLastPosition();
    final long pageOffset = pageBox.getPageOffset();

    if ( logger.isDebugEnabled() ) {
      logger.debug( "PageableRenderer: pageOffset=" + pageOffset + "; nextOffset=" + nextOffset );
    }

    if ( performOutput ) {
      if ( outputProcessor.isNeedAlignedPage() ) {
        final LogicalPageBox box = fillPhysicalPagesStep.compute( pageBox, pageOffset, nextOffset );
        outputProcessor.processContent( box );
        // DebugLog.log("Processing contents for Page " + pageCount + " Page-Offset: " + pageOffset + " -> " +
        // nextOffset);
      } else {
        // DebugLog.log("Processing fast contents for Page " + pageCount + " Page-Offset: " + pageOffset + " -> " +
        // nextOffset);
        outputProcessor.processContent( pageBox );
      }
    } else {
      // todo: When recomputing the contents, we have to update the page cursor or the whole excercise is next to
      // useless ..
      // DebugLog.log("Recomputing contents for Page " + pageCount + " Page-Offset: " + pageOffset + " -> " +
      // nextOffset);
      outputProcessor.processRecomputedContent( pageBox );
    }

    // Now fire the pagebreak. This goes through all layers and informs all
    // components, that a pagebreak has been encountered and possibly a
    // new page has been set. It does not save the state or perform other
    // expensive operations. However, it updates the 'isPagebreakEncountered'
    // flag, which will be active until the input-feed received a new event.
    // Log.debug ("PageTime " + (currentPageAge - lastPageAge));
    final boolean repeat = pageBox.isOpen() || ( pageBox.getHeight() > nextOffset );
    if ( repeat ) {
      pageBox.setPageOffset( nextOffset );
      countBoxesStep.process( pageBox );
      cleanPaginatedBoxesStep.compute( pageBox );
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
      pageBox.setPageOffset( nextOffset );
      outputProcessor.processingFinished();
      return false;
    }
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

  public int getPageCount() {
    return pageCount;
  }

  public boolean isCurrentPageEmpty() {
    // todo: Invent a test that checks whether the page is currently empty.
    final LogicalPageBox logicalPageBox = getPageBox();
    if ( logicalPageBox == null ) {
      throw new IllegalStateException( "LogicalPageBox being null? You messed it up again!" );
    }

    final PageBreakPositionList breakPositionList = logicalPageBox.getAllVerticalBreaks();
    final long masterBreak = breakPositionList.getLastMasterBreak();
    final boolean nextPageContainsContent = ( logicalPageBox.getHeight() > masterBreak );
    return nextPageContainsContent == false;
  }

  public boolean isPageStartPending() {
    return pageStartPending;
  }

  public boolean isPendingPageHack() {
    return true;
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
