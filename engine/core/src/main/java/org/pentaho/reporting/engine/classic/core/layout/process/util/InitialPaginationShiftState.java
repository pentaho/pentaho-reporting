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
