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


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

/**
 * A report task is a generic way to create documents of a certain type from a report object. A task encapsulates all
 * implementation details necessary to perform a generic export. All reports are expected to be written to a repository
 * implementation, which can be a filesystem or any other document storage facility.
 *
 * @author Thomas Morgner
 */
public interface ReportProcessTask extends Runnable {
  /**
   * Defines the content location (and implicitly the repository) for the generated report document.
   *
   * @param body
   *          the content location for the report document.
   */
  public void setBodyContentLocation( final ContentLocation body );

  /**
   * Returns the content location for the generated document.
   *
   * @return the content location where the generated document of the report will be stored.
   */
  public ContentLocation getBodyContentLocation();

  /**
   * Defines the body name generator, that generates name sequences in case the target name is already taken. The given
   * namegenerator should return the first-choice document name as first generated name.
   *
   * @param nameGenerator
   *          the name generator.
   */
  public void setBodyNameGenerator( final NameGenerator nameGenerator );

  /**
   * Returns the name generator for the report document.
   *
   * @return the name generator.
   */
  public NameGenerator getBodyNameGenerator();

  /**
   * Defines the report that will be executed in this task. It is assumed that the report is fully parametrized. Report
   * processing will fail if the report requires parameters that are not given.
   *
   * @param report
   *          the report.
   */
  public void setReport( MasterReport report );

  /**
   * Returns the report that will be executed in this task.
   *
   * @return the report.
   */
  public MasterReport getReport();

  /**
   * Adds a report progress listener that is able to monitor the export progress.
   *
   * @param listener
   *          the listener.
   */
  public void addReportProgressListener( ReportProgressListener listener );

  /**
   * Removes the given report progress listener from the list of listeners.
   *
   * @param listener
   *          the listener.
   */
  public void removeReportProgressListener( ReportProgressListener listener );

  /**
   * Returns the export tasks document mime type.
   *
   * @return the mime type of the report document that is being generated.
   */
  public String getReportMimeType();

  /**
   * Returns any error that has caused the report export to fail.
   *
   * @return the error that caused a failure, or <code>null</code> if there was no error.
   */
  public Throwable getError();

  /**
   * Checks whether the task was aborted. Tasks can be aborted by signaling "interrupt()" to the executing thread.
   *
   * @return true, if the task was aborted, false otherwise.
   */
  public boolean isTaskAborted();

  /**
   * Checks whether the export was successful. An aborted task cannot be successfull.
   *
   * @return true, if the report was exported successfully, false otherwise.
   */
  public boolean isTaskSuccessful();

  /**
   * Returns if the task is configured in a way that the export can be safely started.
   *
   * @return true, if the task is valid and can be started, false otherwise.
   */
  public boolean isValid();
}
