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
