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
