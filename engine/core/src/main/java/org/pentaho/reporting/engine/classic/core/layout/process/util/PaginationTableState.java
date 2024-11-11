/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.FilteringPageBreakPositions;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositions;

public class PaginationTableState implements BasePaginationTableState {
  private PaginationTableState parent;
  private boolean suspended;
  private long pageOffset;
  private long pageHeight;
  private long pageEnd;
  private PageBreakPositions breakPositions;
  private boolean fixedPositionProcessingSuspended;
  private boolean tableProcessing;

  public PaginationTableState( final long pageHeight, final long pageOffset, final long pageEnd,
      final PageBreakPositions breakPositions ) {
    this.pageHeight = pageHeight;
    this.pageOffset = pageOffset;
    this.pageEnd = pageEnd;
    this.breakPositions = breakPositions;
  }

  public PaginationTableState( final PaginationTableState parent ) {
    this.parent = parent;
    this.pageOffset = parent.pageOffset;
    this.breakPositions = parent.breakPositions;
    this.pageHeight = parent.pageHeight;
    this.pageEnd = parent.pageEnd;
    this.fixedPositionProcessingSuspended = true;
    this.tableProcessing = parent.tableProcessing;
  }

  public void suspendVisualStateCollection( final boolean temporary ) {
    this.suspended = true;

    if ( temporary == false && this.parent != null ) {
      this.parent.suspendVisualStateCollection( temporary );
    }
  }

  public boolean isTableProcessing() {
    return tableProcessing;
  }

  public long getPageOffset() {
    return pageOffset;
  }

  public PageBreakPositions getBreakPositions() {
    return breakPositions;
  }

  public long getPageHeight() {
    return pageHeight;
  }

  public long getPageEnd() {
    return pageEnd;
  }

  public boolean isVisualStateCollectionSuspended() {
    if ( suspended ) {
      return true;
    }

    return false;
  }

  public PaginationTableState pop() {
    return parent;
  }

  public boolean isFixedPositionProcessingSuspended() {
    return fixedPositionProcessingSuspended;
  }

  public void defineArtificialPageStart( final long offset ) {
    breakPositions = new FilteringPageBreakPositions( breakPositions, offset );
    pageOffset = offset;
    tableProcessing = true;
  }

  public boolean isOnPageStart( final long offset ) {
    return breakPositions.isPageStart( offset );
  }

  public long getPageOffset( final long position ) {
    return breakPositions.findPreviousBreakPosition( position );
  }
}
