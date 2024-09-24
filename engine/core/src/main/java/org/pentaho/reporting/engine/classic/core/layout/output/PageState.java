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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.process.ProcessState;

import java.io.Serializable;

/**
 * A page state hold a process state along with its current page counter. This is used to save the report state to allow
 * faster pagination later on.
 *
 * @author Thomas Morgner
 */
public class PageState implements Serializable {
  private ProcessState reportState;
  private int pageCursor;
  private Boolean safeToStore;

  public PageState( final ProcessState reportState, final int pageCursor ) {
    if ( reportState == null ) {
      throw new NullPointerException();
    }
    this.reportState = reportState;
    this.pageCursor = pageCursor;
  }

  public int getPageCursor() {
    return pageCursor;
  }

  public ProcessState getReportState() {
    return reportState;
  }

  public void prepareStorage() {
    this.reportState = reportState.deriveForStorage();
  }

  /**
   * Tests whether this state can be saved during the pagination stage. This only returns true if there are no
   * inline-subreports processed in the current state.
   *
   * @return true, if this is a valid safe-state, false otherwise.
   */
  public boolean isSafeToStoreEarly() {
    if ( reportState.getLevel() == LayoutProcess.LEVEL_PAGINATE && reportState.isPrepareRun() == false ) {
      return true;
    }

    if ( pageCursor == 0 ) {
      return true;
    }

    if ( safeToStore == null ) {
      final OutputFunction outputFunction = reportState.getLayoutProcess().getOutputFunction();
      if ( outputFunction instanceof DefaultOutputFunction ) {
        final DefaultOutputFunction defaultOutputFunction = (DefaultOutputFunction) outputFunction;
        safeToStore = defaultOutputFunction.getRenderer().isSafeToStore();
      } else {
        safeToStore = Boolean.FALSE;
      }
    }
    return safeToStore;
  }
}
