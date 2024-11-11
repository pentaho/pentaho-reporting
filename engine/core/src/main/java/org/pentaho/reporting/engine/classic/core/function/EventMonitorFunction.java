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


package org.pentaho.reporting.engine.classic.core.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * A function that logs each event that it receives. This function can be used for debugging purposes.
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public class EventMonitorFunction extends AbstractFunction implements PageEventListener {
  private static final Log logger = LogFactory.getLog( EventMonitorFunction.class );
  /**
   * Counts the number of times the reportStarted(...) method is called.
   */
  private transient int reportStartCount;
  /**
   * A flag indicating whether this expression will receive events from subreports.
   */
  private boolean deepTraversing;

  /**
   * Creates a new function.
   */
  public EventMonitorFunction() {
  }

  /**
   * Creates a new function.
   *
   * @param name
   *          the name of the function
   */
  public EventMonitorFunction( final String name ) {
    setName( name );
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event
   *          the event.
   */
  public void reportStarted( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Report Started: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
    reportStartCount++;
    EventMonitorFunction.logger.info( "Report Started Count: " + reportStartCount );
  }

  /**
   * Receives notification that the report has finished.
   *
   * @param event
   *          the event.
   */
  public void reportFinished( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Report Finished: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event
   *          The event.
   */
  public void reportDone( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Report Done: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );

  }

  /**
   * Receives notification that a page has started.
   *
   * @param event
   *          the event.
   */
  public void pageStarted( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Page Started: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
    EventMonitorFunction.logger.info( "Page Started: " + event.getState().getProcessKey() );
  }

  /**
   * Receives notification that a page has ended.
   *
   * @param event
   *          the event.
   */
  public void pageFinished( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Page Finished: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
    EventMonitorFunction.logger.info( "Page Finished: " + event.getState().getProcessKey() );
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Group Started: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
    EventMonitorFunction.logger.info( "Group Started: " + event.getState().getCurrentGroupIndex() );
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Group Finished: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
    EventMonitorFunction.logger.info( "Group Finished: " + event.getState().getCurrentGroupIndex() );
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Items Advanced: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
  }

  /**
   * Receives notification that a group of item bands is about to be processed.
   *
   * @param event
   *          the event.
   */
  public void itemsStarted( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Items Started: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
  }

  /**
   * Receives notification that a group of item bands has been completed.
   *
   * @param event
   *          the event.
   */
  public void itemsFinished( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Items Finished: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    EventMonitorFunction.logger.info( "Report Initialized: Level = " + event.getState().getLevel() + " ItemCount: "
        + event.getState().getCurrentRow() + " Prepare Run: " + event.getState().isPrepareRun() + " Deep-Event "
        + ( ( event.getType() & ReportEvent.DEEP_TRAVERSING_EVENT ) != 0 ) );
  }

  /**
   * Returns <code>null</code> since this function is for generating log messages only.
   *
   * @return the value of the function (<code>null</code>).
   */
  public Object getValue() {
    return null;
  }

  /**
   * Returns whether this expression will receive events from subreports.
   *
   * @return true, if the function is deep-traversing, false otherwise.
   */
  public boolean isDeepTraversing() {
    return deepTraversing;
  }

  /**
   * Defines, whether this expression will receive events from subreports.
   *
   * @param deepTraversing
   *          true, if the function is deep-traversing, false otherwise.
   */
  public void setDeepTraversing( final boolean deepTraversing ) {
    this.deepTraversing = deepTraversing;
  }
}
