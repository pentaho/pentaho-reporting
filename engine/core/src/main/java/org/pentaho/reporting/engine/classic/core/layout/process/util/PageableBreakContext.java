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
