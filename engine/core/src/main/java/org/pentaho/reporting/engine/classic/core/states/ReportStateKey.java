/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * The process-state key is a unique functional identifier of report-states. However, it does not get updated with
 * changes to the report-state, so it always represents the process-state at the beginning of the report-state
 * processing. This key can be used to see whether there was some progress and to uniquely identify report-states, but
 * for everything else, this class is not suitable.
 *
 * @author Thomas Morgner
 */
public class ReportStateKey {
  private ReportStateKey parent;
  private int cursor;
  private int stateCode;
  private int groupLevel;
  private int subreport;
  private Integer hashCode;
  private int sequenceCounter;
  private boolean restoreState;

  // if true, indicates that an inline-subreport generated this content. Therefore it must be ignored for the
  // purpose of finding visual content to mark page-break positions.
  private boolean inlineSubReportState;

  public ReportStateKey() {
  }

  public ReportStateKey( final ReportStateKey parent, final int cursor, final int stateCode, final int groupLevel,
      final int subreport, final int sequenceCounter, final boolean restoreState, final boolean inlineSubReportState ) {
    this.parent = parent;
    this.cursor = cursor;
    this.stateCode = stateCode;
    this.groupLevel = groupLevel;
    this.subreport = subreport;
    this.sequenceCounter = sequenceCounter;
    this.restoreState = restoreState;
    this.inlineSubReportState = inlineSubReportState;
  }

  /**
   * This is just a debugging help to identify repeated runs of the same report sequence. It is not used to compare
   * equality of these keys.
   *
   * @return
   */
  public int getSequenceCounter() {
    return sequenceCounter;
  }

  public ReportStateKey getParent() {
    return parent;
  }

  public int getCursor() {
    return cursor;
  }

  public int getStateCode() {
    return stateCode;
  }

  public int getGroupLevel() {
    return groupLevel;
  }

  public int getSubreport() {
    return subreport;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ReportStateKey that = (ReportStateKey) o;

    if ( restoreState != that.restoreState ) {
      return false;
    }
    if ( cursor != that.cursor ) {
      return false;
    }
    if ( groupLevel != that.groupLevel ) {
      return false;
    }
    if ( stateCode != that.stateCode ) {
      return false;
    }
    if ( subreport != that.subreport ) {
      return false;
    }
    if ( parent != null ? !parent.equals( that.parent ) : that.parent != null ) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    if ( hashCode == null ) {
      int result = ( parent != null ? parent.hashCode() : 0 );
      result = 29 * result + cursor;
      result = 29 * result + stateCode;
      result = 29 * result + groupLevel;
      result = 29 * result + subreport;
      result = 29 * result + ( restoreState ? 1 : 0 );
      result = 29 * result + ( inlineSubReportState ? 1 : 0 );
      // noinspection UnnecessaryBoxing
      hashCode = Integer.valueOf( result );
      return result;
    }
    return hashCode.intValue();
  }

  public boolean isInlineSubReportState() {
    return inlineSubReportState;
  }

  public String toString() {
    return "ReportStateKey{" + "sc=" + sequenceCounter + ", cursor=" + cursor + ", groupLevel=" + groupLevel
        + ", subreport=" + subreport + ", stateCode=" + ReportEvent.translateStateCode( stateCode ) + ", restoreState="
        + restoreState + ", inlineSubReport=" + inlineSubReportState + ", stateCodeRaw=0x"
        + Integer.toHexString( stateCode ) + ", parent=" + parent + '}';
  }
}
