/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class BlockLevelPaginationShiftState implements PaginationShiftState {
  private PaginationShiftState parent;
  private long shift;
  private long initialShift;
  private boolean breakSuspended;
  private StackedObjectPool<BlockLevelPaginationShiftState> pool;
  private RenderBox box;

  public BlockLevelPaginationShiftState() {
  }

  public void reuse( final StackedObjectPool<BlockLevelPaginationShiftState> pool, final PaginationShiftState parent,
      final RenderBox box ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.pool = pool;
    this.parent = parent;
    this.initialShift = parent.getShiftForNextChild();
    this.shift = initialShift;
    this.breakSuspended = parent.isManualBreakSuspendedForChilds();
    this.box = box;
  }

  public void suspendManualBreaks() {
    breakSuspended = true;
  }

  public boolean isManualBreakSuspended() {
    return parent.isManualBreakSuspendedForChilds();
  }

  public boolean isManualBreakSuspendedForChilds() {
    return breakSuspended;
  }

  public void updateShiftFromChild( final long absoluteValue ) {
    setShift( absoluteValue );
  }

  public void increaseShift( final long value ) {
    if ( value < 0 ) {
      throw new IllegalStateException();
    }
    this.shift += value;
  }

  public long getShiftForNextChild() {
    return shift;
  }

  public void setShift( final long value ) {
    if ( value < shift ) {
      throw new IllegalStateException( "Cannot shift backwards" );
    }

    this.shift = value;
  }

  public PaginationShiftState pop( InstanceID id ) {
    if ( box != null && id != box.getInstanceId() ) {
      throw new IllegalStateException();
    }

    long effectiveShift = this.shift;
    if ( box != null ) {

      if ( box.getParent() != null ) {
        final long shiftRaw = shift - initialShift;
        effectiveShift = box.getParent().extendHeight( box, shiftRaw ) + initialShift;
        if ( effectiveShift != initialShift ) {
          box.getParent().markApplyStateDirty();
        }
      }
    }

    parent.updateShiftFromChild( effectiveShift );

    if ( this.pool != null ) {
      this.pool.free( this );
      this.pool = null;
    }

    this.box = null;
    return parent;
  }
}
