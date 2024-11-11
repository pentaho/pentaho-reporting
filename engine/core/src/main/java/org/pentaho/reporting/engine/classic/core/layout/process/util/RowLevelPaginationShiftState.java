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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class RowLevelPaginationShiftState implements PaginationShiftState {
  private PaginationShiftState parent;
  private long shift;
  private long shiftForChilds;
  private long initialShift;
  private StackedObjectPool<RowLevelPaginationShiftState> pool;
  private RenderBox box;

  public RowLevelPaginationShiftState() {
  }

  public void reuse( final StackedObjectPool<RowLevelPaginationShiftState> pool, final PaginationShiftState parent,
      final RenderBox box ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.parent = parent;
    this.pool = pool;
    this.shiftForChilds = parent.getShiftForNextChild();
    this.shift = this.shiftForChilds;
    this.initialShift = this.shift;
    this.box = box;
  }

  public void suspendManualBreaks() {
  }

  public boolean isManualBreakSuspended() {
    return parent.isManualBreakSuspendedForChilds();
  }

  public boolean isManualBreakSuspendedForChilds() {
    return true;
  }

  public void updateShiftFromChild( final long absoluteValue ) {
    this.shift = Math.max( shift, absoluteValue );
  }

  public long getShiftForNextChild() {
    return shiftForChilds;
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

  public void increaseShift( final long value ) {
    this.shiftForChilds = Math.max( shiftForChilds, this.shiftForChilds + value );
    this.shift = Math.max( shift, shiftForChilds );
  }

  public void setShift( final long value ) {
    this.shiftForChilds = Math.max( shiftForChilds, value );
    this.shift = Math.max( shift, shiftForChilds );
  }
}
