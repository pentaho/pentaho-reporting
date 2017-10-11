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

package org.pentaho.reporting.engine.classic.core.event;

import java.util.EventListener;

/**
 * ReportListeners get informed whenever the state of a report changes.
 * <P>
 * You should be aware that most events occur more than once. For example, the reportStarted event will be triggered
 * every time the report is regenerated.
 * <P>
 * When handling these events, use the reportState to track the current changes of the report.
 *
 * @author Thomas Morgner
 */
public interface ReportListener extends EventListener {
  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( ReportEvent event );

  /**
   * Receives notification that report generation has started.
   * <P>
   * The event carries a ReportState.Started state. Use this to prepare the report header.
   *
   * @param event
   *          The event.
   */
  public void reportStarted( ReportEvent event );

  /**
   * Receives notification that report generation has finished (the last record is read and all groups are closed).
   *
   * @param event
   *          The event.
   */
  public void reportFinished( ReportEvent event );

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event
   *          The event.
   */
  public void reportDone( ReportEvent event );

  /**
   * Receives notification that a new group has started.
   * <P>
   * The group can be determined by the report state's getCurrentGroup() function.
   *
   * @param event
   *          The event.
   */
  public void groupStarted( ReportEvent event );

  /**
   * Receives notification that a group is finished.
   * <P>
   * The group can be determined by the report state's getCurrentGroup() function.
   *
   * @param event
   *          The event.
   */
  public void groupFinished( ReportEvent event );

  /**
   * Receives notification that a group of item bands is about to be processed.
   * <P>
   * The next events will be itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event
   *          The event.
   */
  public void itemsStarted( ReportEvent event );

  /**
   * Receives notification that a group of item bands has been completed.
   * <P>
   * The itemBand is finished, the report starts to close open groups.
   *
   * @param event
   *          The event.
   */
  public void itemsFinished( ReportEvent event );

  /**
   * Receives notification that a new row has been read.
   * <P>
   * This event is raised before an ItemBand is printed.
   *
   * @param event
   *          The event.
   */
  public void itemsAdvanced( ReportEvent event );

  /**
   * A crosstab specific event notifying crosstab-aware functions to select the result for the summary row cell that
   * will be printed next.
   *
   * @param event
   *          The report event.
   */
  public void summaryRowSelection( ReportEvent event );
}
