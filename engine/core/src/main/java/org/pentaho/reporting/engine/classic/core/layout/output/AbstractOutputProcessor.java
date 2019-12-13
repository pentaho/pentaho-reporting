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
 * Copyright (c) 2001 - 2019 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.libraries.base.util.MemoryUsageMessage;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

public abstract class AbstractOutputProcessor implements OutputProcessor {
  private static final Log logger = LogFactory.getLog( AbstractOutputProcessor.class );

  private static final class PageSafeGuardingInformation {
    private final long footerHeight;
    private final long headerHeight;

    public PageSafeGuardingInformation( long headerHeight, long footerHeight ) {
      this.footerHeight = footerHeight;
      this.headerHeight = headerHeight;
    }

    @Override
    public boolean equals( Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }
      PageSafeGuardingInformation that = (PageSafeGuardingInformation) o;
      return footerHeight == that.footerHeight && headerHeight == that.headerHeight;
    }

    @Override
    public int hashCode() {
      return Objects.hash( footerHeight, headerHeight );
    }

    @Override
    public String toString() {
      return "PageSafeGuardingInformation{headerHeight=" + headerHeight + ", footerHeight=" + footerHeight + '}';
    }
  }

  protected static final int PROCESSING_PAGES = 0;
  protected static final int PROCESSING_CONTENT = 2;
  private int processingState;

  private List<LogicalPageKey> logicalPages;
  private List<PageSafeGuardingInformation> logicalPageSafeGuard;
  private int pageCursor;
  private long startTime;

  protected AbstractOutputProcessor() {
    logicalPages = new ArrayList<LogicalPageKey>();
    logicalPageSafeGuard = new ArrayList<>();
  }

  public void processingStarted( final ReportDefinition report, final ProcessingContext processingContext ) {
    startTime = processingContext.getReportProcessingStartTime();
    logPerformance( "Time to Pagination" );
  }

  protected long getStartTime() {
    return startTime;
  }

  public final int getLogicalPageCount() {
    return logicalPages.size();
  }

  public final LogicalPageKey getLogicalPage( final int page ) {
    if ( isPaginationFinished() == false ) {
      throw new IllegalStateException();
    }

    return logicalPages.get( page );
  }

  /**
   * Checks whether the 'processingFinished' event had been received at least once.
   *
   * @return
   */
  public final boolean isPaginationFinished() {
    return processingState == AbstractOutputProcessor.PROCESSING_CONTENT;
  }

  /**
   * Notifies the output processor, that the processing has been finished and that the input-feed received the last
   * event.
   */
  public final void processingFinished() {
    if ( processingState == AbstractOutputProcessor.PROCESSING_PAGES ) {
      // the pagination is complete. So, now we can produce real content.
      processingPagesFinished();
      logPerformance( "Pagination finished" );
      processingState = AbstractOutputProcessor.PROCESSING_CONTENT;
    } else {
      processingContentFinished();
      logPerformance( "Content Processing finished" );
    }
    pageCursor = 0;
  }

  private void logPerformance( final String message ) {
    final long time = System.currentTimeMillis();
    final double deltaTime = time - getStartTime();

    final double rowsPerSec = ( getPageCursor() * 1000.0 / deltaTime );
    if ( logger.isDebugEnabled() ) {
      final FastMessageFormat messageFormat =
          new FastMessageFormat(
              "{0} - Pages: {1} - Time: {2,number,0.000}sec - Throughput: ({3,number,0.000} rows/sec) " );
      logger.debug( new MemoryUsageMessage( messageFormat.format( new Object[] { message, getPageCursor(),
        deltaTime / 1000.0, rowsPerSec } ) ) );
    }
  }

  protected void processingContentFinished() {

  }

  protected void processingPagesFinished() {
    logicalPages = Collections.unmodifiableList( logicalPages );
  }

  protected LogicalPageKey createLogicalPage( final int width, final int height ) {
    return new LogicalPageKey( logicalPages.size(), width, height );
  }

  public final int getPageCursor() {
    return pageCursor;
  }

  public final void setPageCursor( final int pageCursor ) {
    this.pageCursor = pageCursor;
  }

  /**
   * This flag indicates, whether the output processor has collected enough information to start the content generation.
   *
   * @return
   */
  protected boolean isContentGeneratable() {
    return processingState == AbstractOutputProcessor.PROCESSING_CONTENT;
  }

  public void processRecomputedContent( final LogicalPageBox pageBox ) throws ContentProcessingException {
    setPageCursor( pageCursor + 1 );
  }

  public final void processContent( final LogicalPageBox logicalPage ) throws ContentProcessingException {
    if ( isContentGeneratable() == false ) {
      // This is the pagination stage ..

      // This is just an assertation ...
      // Only if pagination is active ..
      final PageGrid pageGrid = logicalPage.getPageGrid();
      final int rowCount = pageGrid.getRowCount();
      final int colCount = pageGrid.getColumnCount();

      final LogicalPageKey key = createLogicalPage( colCount, rowCount );
      logicalPages.add( key );
      logicalPageSafeGuard.add(
              new PageSafeGuardingInformation( logicalPage.getHeaderArea().getHeight(),
              logicalPage.getFooterArea().getHeight() ) );

      final int pageCursor = getPageCursor();
      if ( key.getPosition() != pageCursor ) {
        throw new IllegalStateException( "Expected position " + pageCursor + " is not the key's position "
            + key.getPosition() );
      }

      processPaginationContent( key, logicalPage );
      setPageCursor( pageCursor + 1 );

    } else { // if (isContentGeneratable())
      // This is the content generation stage ..

      final int pageCursor = getPageCursor();
      final LogicalPageKey logicalPageKey = getLogicalPage( pageCursor );
      PageSafeGuardingInformation current =
              new PageSafeGuardingInformation( logicalPage.getHeaderArea().getHeight(),
              logicalPage.getFooterArea().getHeight() );
      PageSafeGuardingInformation recorded = logicalPageSafeGuard.get( pageCursor );
      if ( !current.equals( recorded ) ) {
        throw new ContentProcessingException( String.format(
          "This reports reserved page header or footer height has changed since computing "
            + "pagination information. This report will likely fail. [Expected: %s but found %s]",
          recorded, current ) );
      }

      processPageContent( logicalPageKey, logicalPage );
      setPageCursor( pageCursor + 1 );
    }
  }

  public boolean isNeedAlignedPage() {
    return isContentGeneratable();
  }

  protected void processPaginationContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
    throws ContentProcessingException {
  }

  protected abstract void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
    throws ContentProcessingException;

  public int getPhysicalPageCount() {
    // By default, we assume a one-to-one mapping between logical and physical pages. Only the pageable target
    // will implement a different mapping ..
    return getLogicalPageCount();
  }
}
