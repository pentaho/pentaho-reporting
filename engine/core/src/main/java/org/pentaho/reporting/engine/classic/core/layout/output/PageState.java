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
