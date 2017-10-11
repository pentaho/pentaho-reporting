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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

public class FastPageStateList implements PageStateList {
  private int size;
  private PageState initialState;
  private ReportProcessor reportProcessor;

  public FastPageStateList( final ReportProcessor reportProcessor ) {
    if ( reportProcessor == null ) {
      throw new NullPointerException();
    }
    this.reportProcessor = reportProcessor;
  }

  public int size() {
    return size;
  }

  public void add( final PageState state ) {
    if ( size == 0 ) {
      state.prepareStorage();
      initialState = state;
    }
    size += 1;
  }

  public void clear() {
    initialState = null;
    size = 0;

  }

  public PageState get( final int index ) {
    if ( index == 0 ) {
      return initialState;
    }

    try {
      PageState state = initialState;
      for ( int i = 0; i <= index; i++ ) {
        state = reportProcessor.processPage( state, false );
        if ( state == null ) {
          throw new IllegalStateException( "State returned is null: Report processing reached premature end-point." );
        }
      }
      return state;
    } catch ( ReportProcessingException e ) {
      throw new IllegalStateException( "State restoration failed." );
    }
  }
}
