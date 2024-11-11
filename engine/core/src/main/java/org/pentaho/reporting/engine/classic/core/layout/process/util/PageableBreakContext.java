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

public class PageableBreakContext {
  private long shift;
  // this initialShift is used only for debugging purposes ..
  private long initialShift;
  private long appliedShift;
  private long heightExtension;
  private boolean breakSuspended;

  public PageableBreakContext() {
  }

  public void updateFromParent( final PageableBreakContext parent, final boolean useInitialShift ) {
    if ( useInitialShift ) {
      this.shift = parent.appliedShift;
      this.appliedShift = parent.appliedShift;
      this.initialShift = parent.appliedShift;
    } else {
      this.shift = parent.shift;
      this.appliedShift = parent.shift;
      this.initialShift = parent.shift;
    }

    this.heightExtension = 0;
    this.breakSuspended = parent.breakSuspended;
  }

  public long getShift() {
    return shift;
  }

  public void setShift( final long shift ) {
    if ( this.shift > shift ) {
      throw new IllegalStateException( "Cannot undo previous shifting" );
    }

    this.shift = shift;
  }

  public long getAppliedShift() {
    return appliedShift;
  }

  public void setAppliedShift( final long appliedShift ) {
    this.appliedShift = appliedShift;
  }

  public long getInitialShift() {
    return initialShift;
  }

  public boolean isBreakSuspended() {
    return breakSuspended;
  }

  public void suspendBreaks() {
    breakSuspended = true;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "PageableBreakContext" );
    sb.append( "{shift=" ).append( shift );
    sb.append( ", initialShift=" ).append( initialShift );
    sb.append( ", breakSuspended=" ).append( breakSuspended );
    sb.append( '}' );
    return sb.toString();
  }

  public long getHeightExtension() {
    return heightExtension;
  }

  public void setHeightExtension( final long heightExtension ) {
    this.heightExtension = heightExtension;
  }

  public void reset() {
    breakSuspended = false;
    heightExtension = 0;
    shift = 0;
    appliedShift = 0;
    initialShift = 0;
  }

  public void commitShift() {
    shift = appliedShift + heightExtension;
  }
}
