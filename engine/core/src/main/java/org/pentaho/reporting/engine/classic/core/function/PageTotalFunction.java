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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

import java.util.HashMap;

/**
 * Prints the total number of pages of an report. If a group is specified, this function expects the group to have the
 * manual pagebreak enabled.
 * <p/>
 * This function will only work as expected in group mode if the named group has pagebreak-before set to true on the
 * header for pagebreak-after set to true on the group's footer.
 *
 * @author Thomas Morgner
 */
public class PageTotalFunction extends PageFunction {
  /**
   * A map of results, keyed by the process-key.
   */
  private HashMap groupPages;

  /**
   * The state key is used to store the result for the report.
   */
  private transient ReportStateKey currentStateKey;
  /**
   * An internal state flag.
   */
  private boolean firstGroupSeen;

  /**
   * Creates a new page total function.
   */
  public PageTotalFunction() {
    this.groupPages = new HashMap();
    setDependencyLevel( 0 );
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event
   *          the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    super.reportInitialized( event );
    if ( event.isDeepTraversing() ) {
      return;
    }

    firstGroupSeen = false;
    currentStateKey = event.getState().getProcessKey();
  }

  public void groupStarted( final ReportEvent event ) {
    super.groupStarted( event );
    if ( event.isDeepTraversing() ) {
      return;
    }

    if ( getGroup() == null ) {
      return;
    }

    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      if ( firstGroupSeen == false ) {
        firstGroupSeen = true;
        return;
      }

      currentStateKey = event.getState().getProcessKey();
    }
  }

  public void groupFinished( final ReportEvent event ) {
    super.groupFinished( event );
    if ( event.isDeepTraversing() ) {
      return;
    }

    if ( getGroup() == null ) {
      return;
    }

    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      if ( event.getState().isPrepareRun() ) {
        groupPages.put( currentStateKey, super.getValue() );
      }
    }
  }

  public void pageFinished( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      groupPages.put( currentStateKey, super.getValue() );
    }
  }

  public void reportDone( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }

    if ( event.getState().isPrepareRun() ) {
      groupPages.put( currentStateKey, super.getValue() );
    }
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final PageTotalFunction function = (PageTotalFunction) super.getInstance();
    function.groupPages = new HashMap();
    return function;
  }

  public Object getValue() {
    return groupPages.get( currentStateKey );
  }

}
