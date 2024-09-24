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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

import javax.swing.event.EventListenerList;

/**
 * A base class for common report process task implementations.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractReportProcessTask implements ReportProcessTask {
  private EventListenerList listeners;
  private ContentLocation bodyContentLocation;
  private MasterReport report;
  private Throwable error;
  private NameGenerator bodyNameGenerator;

  protected AbstractReportProcessTask() {
    this.listeners = new EventListenerList();
  }

  /**
   * Returns the content location for the generated document.
   *
   * @return the content location where the generated document of the report will be stored.
   */
  public ContentLocation getBodyContentLocation() {
    return bodyContentLocation;
  }

  /**
   * Defines the content location (and implicitly the repository) for the generated report document.
   *
   * @param bodyContentLocation
   *          the content location for the report document.
   */
  public void setBodyContentLocation( final ContentLocation bodyContentLocation ) {
    this.bodyContentLocation = bodyContentLocation;
  }

  /**
   * Returns the name generator for the report document.
   *
   * @return the name generator.
   */
  public NameGenerator getBodyNameGenerator() {
    return bodyNameGenerator;
  }

  /**
   * Defines the body name generator, that generates name sequences in case the target name is already taken. The given
   * namegenerator should return the first-choice document name as first generated name.
   *
   * @param bodyNameGenerator
   *          the name generator.
   */
  public void setBodyNameGenerator( final NameGenerator bodyNameGenerator ) {
    this.bodyNameGenerator = bodyNameGenerator;
  }

  /**
   * Returns the report that will be executed in this task.
   *
   * @return the report.
   */
  public MasterReport getReport() {
    return report;
  }

  /**
   * Defines the report that will be executed in this task. It is assumed that the report is fully parametrized. Report
   * processing will fail if the report requires parameters that are not given.
   *
   * @param report
   *          the report.
   */
  public void setReport( final MasterReport report ) {
    this.report = report;
  }

  /**
   * Adds a report progress listener that is able to monitor the export progress.
   *
   * @param listener
   *          the listener.
   */
  public void addReportProgressListener( final ReportProgressListener listener ) {
    listeners.add( ReportProgressListener.class, listener );
  }

  /**
   * Removes the given report progress listener from the list of listeners.
   *
   * @param listener
   *          the listener.
   */
  public void removeReportProgressListener( final ReportProgressListener listener ) {
    listeners.remove( ReportProgressListener.class, listener );
  }

  /**
   * Returns all progress listeners that are registered on this ProcessTask implementation.
   *
   * @return all registered listeners.
   */
  protected ReportProgressListener[] getReportProgressListeners() {
    return listeners.getListeners( ReportProgressListener.class );
  }

  /**
   * Returns any error that has caused the report export to fail.
   *
   * @return the error that caused a failure, or <code>null</code> if there was no error.
   */
  public Throwable getError() {
    return error;
  }

  /**
   * Updates the error cause.
   *
   * @param error
   *          the error.
   */
  protected void setError( final Throwable error ) {
    this.error = error;
  }

  /**
   * Checks whether the task was aborted. Tasks can be aborted by signaling "interrupt()" to the executing thread.
   *
   * @return true, if the task was aborted, false otherwise.
   */
  public boolean isTaskAborted() {
    return this.error instanceof InterruptedException;
  }

  /**
   * Checks whether the export was successful. An aborted task cannot be successfull.
   *
   * @return true, if the report was exported successfully, false otherwise.
   */
  public boolean isTaskSuccessful() {
    return this.error == null;
  }

  /**
   * Returns if the task is configured in a way that the export can be safely started.
   *
   * @return true, if the task is valid and can be started, false otherwise.
   */
  public boolean isValid() {
    return this.report != null && this.bodyContentLocation != null && this.bodyNameGenerator != null;
  }
}
