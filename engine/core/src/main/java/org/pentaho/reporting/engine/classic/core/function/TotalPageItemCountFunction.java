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

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A report function that counts the total number of items contained in groups in a report. Resets the counter with each
 * new page, and with each new group if the optional group parameter is specified. The function will always reset with
 * each new page, so if a group spans across a page break the counter will be still be reset.
 * <p/>
 * Like all Total-Functions, this function produces precomputed totals. The function's result is precomputed once and
 * will not change later.
 * <p/>
 * The ItemCount can be used to produce a running row-count for a group or report.
 * <p/>
 * To count the number of groups in a report, use the TotalGroupCountFunction.
 *
 * @author Thomas Morgner
 */
public class TotalPageItemCountFunction extends TotalItemCountFunction implements PageEventListener {

  /**
   * holds the collection of values associated with pages and groups
   */
  private transient PageGroupValues values;

  private int pageIndex = 0;

  public TotalPageItemCountFunction() {
    values = new PageGroupValues();
  }

  protected boolean isPrepareRunLevel( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() && event.getState().getLevel() == LayoutProcess.LEVEL_PAGINATE ) {
      return true;
    }
    return false;
  }

  /**
   * If this is the group associated with the function, store away the final value
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      storeValue( event );
    }
  }

  /**
   * Handles the pageStartedEvent.
   *
   * @param event
   *          the report event.
   */
  public void pageStarted( final ReportEvent event ) {
    pageIndex++;
    clear();
  }

  /**
   * Handles the pageFinishedEvent. Stores the current page value and clears the counter. pageFinished can be hit
   * multiple times for a single page, but the stored value should be consistent.
   *
   * @param event
   *          the report event.
   */
  public void pageFinished( final ReportEvent event ) {
    storeValue( event );
  }

  public Object getValue() {
    return values.get( pageIndex, currentGroupKey );
  }

  private void storeValue( final ReportEvent event ) {
    if ( isPrepareRunLevel( event ) ) {
      values.put( pageIndex, currentGroupKey, super.getValue() );
    }
  }

  /**
   * Return a completly separated copy of this function. The copy no longer shares any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TotalPageItemCountFunction function = (TotalPageItemCountFunction) super.getInstance();
    function.values = new PageGroupValues();
    return function;
  }

  /**
   * Helper function for the serialization.
   *
   * @param in
   *          the input stream.
   * @throws java.io.IOException
   *           if an IO error occured.
   * @throws ClassNotFoundException
   *           if a required class could not be found.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    values = new PageGroupValues();
  }
}
