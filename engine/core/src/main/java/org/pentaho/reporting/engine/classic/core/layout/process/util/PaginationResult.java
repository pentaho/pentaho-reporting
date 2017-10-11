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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * Creation-Date: 02.05.2007, 14:55:58
 *
 * @author Thomas Morgner
 */
public final class PaginationResult {
  private PageBreakPositionList allBreaks;
  private boolean overflow;
  private boolean nextPageContainsContent;
  private ReportStateKey lastVisibleState;

  public PaginationResult( final PageBreakPositionList allBreaks, final boolean overflow,
      final boolean nextPageContainsContent, final ReportStateKey lastVisibleState ) {
    if ( allBreaks == null ) {
      throw new NullPointerException();
    }
    this.nextPageContainsContent = nextPageContainsContent;
    this.allBreaks = allBreaks;
    this.overflow = overflow;
    this.lastVisibleState = lastVisibleState;
  }

  public boolean isNextPageContainsContent() {
    return nextPageContainsContent;
  }

  public ReportStateKey getLastVisibleState() {
    return lastVisibleState;
  }

  public PageBreakPositionList getAllBreaks() {
    return allBreaks;
  }

  public boolean isOverflow() {
    return overflow;
  }

  public long getLastPosition() {
    return allBreaks.getLastMasterBreak();
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "PaginationResult" );
    sb.append( "{lastVisibleState=" ).append( lastVisibleState );
    sb.append( ", nextPageContainsContent=" ).append( nextPageContainsContent );
    sb.append( ", overflow=" ).append( overflow );
    sb.append( ", lastPosition=" ).append( getLastPosition() );
    sb.append( ", allBreaks=" ).append( allBreaks );
    sb.append( '}' );
    return sb.toString();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final PaginationResult that = (PaginationResult) o;

    if ( nextPageContainsContent != that.nextPageContainsContent ) {
      return false;
    }
    if ( overflow != that.overflow ) {
      return false;
    }
    if ( !allBreaks.equals( that.allBreaks ) ) {
      return false;
    }
    /*
     * if (lastVisibleState != null ? !lastVisibleState.equals(that.lastVisibleState) : that.lastVisibleState != null) {
     * return false; }
     */
    return true;
  }

  public int hashCode() {
    int result = allBreaks.hashCode();
    result = 31 * result + ( overflow ? 1 : 0 );
    result = 31 * result + ( nextPageContainsContent ? 1 : 0 );
    // result = 31 * result + (lastVisibleState != null ? lastVisibleState.hashCode() : 0);
    return result;
  }
}
