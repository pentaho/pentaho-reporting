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
