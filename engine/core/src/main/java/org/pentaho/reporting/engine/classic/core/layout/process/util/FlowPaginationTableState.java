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

public class FlowPaginationTableState implements BasePaginationTableState {
  private FlowPaginationTableState parent;
  private boolean suspended;
  private long pageOffset;
  private PageBreakPositions breakPositions;
  private boolean tableProcessing;

  public FlowPaginationTableState( final long pageOffset, final PageBreakPositions breakPositions ) {
    this.pageOffset = pageOffset;
    this.breakPositions = breakPositions;
  }

  public FlowPaginationTableState( final FlowPaginationTableState parent ) {
    this.parent = parent;
    this.pageOffset = parent.pageOffset;
    this.breakPositions = parent.breakPositions;
    this.tableProcessing = parent.tableProcessing;
  }

  public boolean isTableProcessing() {
    return tableProcessing;
  }

  public void suspendVisualStateCollection( final boolean temporary ) {
    this.suspended = true;

    if ( temporary == false && this.parent != null ) {
      this.parent.suspendVisualStateCollection( temporary );
    }
  }

  public long getPageOffset() {
    return pageOffset;
  }

  public PageBreakPositions getBreakPositions() {
    return breakPositions;
  }

  public boolean isVisualStateCollectionSuspended() {
    if ( suspended ) {
      return true;
    }

    return false;
  }

  public FlowPaginationTableState pop() {
    return parent;
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
