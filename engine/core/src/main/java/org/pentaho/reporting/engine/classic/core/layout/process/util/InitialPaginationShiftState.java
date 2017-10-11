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

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class InitialPaginationShiftState implements PaginationShiftState {
  private long shift;
  private boolean breakSuspended;

  public InitialPaginationShiftState() {
  }

  public boolean isManualBreakSuspended() {
    return false;
  }

  public void suspendManualBreaks() {
    breakSuspended = true;
  }

  public boolean isManualBreakSuspendedForChilds() {
    return breakSuspended;
  }

  public long getShiftForNextChild() {
    return shift;
  }

  public PaginationShiftState pop( InstanceID id ) {
    throw new UnsupportedOperationException();
  }

  public void updateShiftFromChild( final long absoluteValue ) {
    setShift( absoluteValue );
  }

  public void increaseShift( final long value ) {
    if ( value < 0 ) {
      throw new IllegalArgumentException();
    }
    this.shift += value;
  }

  public void setShift( final long value ) {
    if ( value < shift ) {
      throw new IllegalArgumentException();
    }
    this.shift = value;
  }
}
