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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

/**
 * A report function that counts pages. This method is only useable when a report processor is used, which generated
 * PageEvents. The PageableReportProcessor is one of them.
 * <p/>
 * As with all page dependent functions: The function will only be active, when the page events get fired, this usually
 * only happens during the last pagination run and the printing. The function level will be negative when this happens.
 * <p/>
 *
 * @author Thomas Morgner
 */
public class PageFunction extends AbstractFunction implements PageEventListener {
  /**
   * The current page.
   */
  private transient int page;

  /**
   * The name of the group on which to reset the count. This can be set to null to compute the page count for the whole
   * report.
   */
  private String group;
  /**
   * The initial page number on which the counting should start.
   */
  private int startPage;
  /**
   * The increment by which the page number should be increases each time.
   */
  private int pageIncrement;
  /**
   * The dependency level of the function.
   */
  private int dependencyLevel;
  /**
   * An internal state-management flag.
   */
  private boolean ignoreNextPageStart;
  /**
   * An internal state-management flag.
   */
  private boolean ignoreNextGroupStart;

  /**
   * Constructs an unnamed function.
   * <P>
   * This constructor is intended for use by the SAX handler class only.
   */
  public PageFunction() {
    this.startPage = 1;
    this.pageIncrement = 1;
    this.dependencyLevel = LayoutProcess.LEVEL_PAGINATE;
  }

  /**
   * Constructs a named function.
   *
   * @param name
   *          the function name.
   */
  public PageFunction( final String name ) {
    this();
    setName( name );
  }

  /**
   * Checks whether this expression is a deep-traversing expression. Deep-traversing expressions receive events from all
   * sub-reports. This returns true, as this function has to receive page-events even if a sub-report is currently being
   * processed.
   *
   * @return true.
   */
  public boolean isDeepTraversing() {
    return true;
  }

  /**
   * Returns the defined dependency level. For page functions, this level can be as low as the pagination level.
   *
   * @return the dependency level.
   */
  public int getDependencyLevel() {
    return dependencyLevel;
  }

  /**
   * Defines the defined dependency level. For page functions, this level can be as low as the pagination level.
   *
   * @param dependencyLevel
   *          the dependency level.
   */
  public void setDependencyLevel( final int dependencyLevel ) {
    if ( dependencyLevel < LayoutProcess.LEVEL_PAGINATE ) {
      throw new IllegalArgumentException(
          "PageFunction.setDependencyLevel(...) : A dependency level lower than paginate is not allowed." );
    }
    this.dependencyLevel = dependencyLevel;
  }

  /**
   * Returns the page increment.
   *
   * @return the page increment.
   */
  public int getPageIncrement() {
    return pageIncrement;
  }

  /**
   * Defines the page increment.
   *
   * @param pageIncrement
   *          the page increment.
   */
  public void setPageIncrement( final int pageIncrement ) {
    this.pageIncrement = pageIncrement;
  }

  /**
   * Returns the group name.
   *
   * @return the group name.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Sets the name of the group that the function acts upon.
   *
   * @param group
   *          the group name.
   */
  public void setGroup( final String group ) {
    this.group = group;
  }

  /**
   * Returns the page number where the counting starts.
   *
   * @return the page number of the first page.
   */
  public int getStartPage() {
    return this.startPage;
  }

  /**
   * Defines the page number where the counting starts.
   *
   * @param startPage
   *          the page number of the first page.
   */
  public void setStartPage( final int startPage ) {
    this.startPage = startPage;
  }

  /**
   * Returns the current page.
   *
   * @return the current page.
   */
  public int getPage() {
    return page;
  }

  /**
   * Sets the current page.
   *
   * @param page
   *          the page.
   */
  protected void setPage( final int page ) {
    this.page = page;
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event
   *          the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }
    // Next event in list will be the first page-started event. Will set the page number again..
    this.setPage( getStartPage() );
    ignoreNextGroupStart = true;
    ignoreNextPageStart = false;
    // waitForNextGroupStart = false;
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    // The defined group is only valid for the master-report.
    if ( event.isDeepTraversing() ) {
      return;
    }
    // if we have no defined group, no need to do anything else.
    if ( getGroup() == null ) {
      return;
    }

    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      // waitForNextGroupStart = false;

      if ( ignoreNextGroupStart ) {
        // The report-header had been printed recently. Add it to the group's list of pages
        ignoreNextGroupStart = false;
        return;
      }

      ignoreNextPageStart = true;
      // Unconditionally reset the page counter. For groups not starting with a new page, the
      // behaviour of the function is undefined.
      this.setPage( getStartPage() );
    }
  }

  /**
   * Receives notification from the report engine that a new page is starting. Grabs the page number from the report
   * state and stores it.
   *
   * @param event
   *          the event.
   */
  public void pageStarted( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.REPORT_INITIALIZED ) == ReportEvent.REPORT_INITIALIZED ) {
      if ( event.isDeepTraversing() == false ) {
        this.setPage( getStartPage() );
        return;
      }
    }
    if ( ignoreNextPageStart ) {
      ignoreNextPageStart = false;
      return;
    }
    // if (waitForNextGroupStart &&
    // (event.getType() & ReportEvent.GROUP_STARTED) == ReportEvent.GROUP_STARTED)
    // {
    // this.setPage(getStartPage());
    // this.waitForNextGroupStart = false;
    // return;
    // }

    setPage( getPage() + getPageIncrement() );
  }

  /**
   * Returns, whether the next page-start event will be ignored. This is an internal state flag to keep the computation
   * in sync with the events.
   *
   * @return the flag.
   */
  protected boolean isIgnoreNextPageStart() {
    return ignoreNextPageStart;
  }

  /**
   * Receives notification that a page is completed.
   *
   * @param event
   *          The event.
   */
  public void pageFinished( final ReportEvent event ) {
    // ignored ...
  }

  //
  // public void groupFinished(final ReportEvent event)
  // {
  // // The defined group is only valid for the master-report.
  // if (event.isDeepTraversing())
  // {
  // return;
  // }
  // // if we have no defined group, no need to do anything else.
  // if (getGroup() == null)
  // {
  // return;
  // }
  //
  // // if (FunctionUtilities.isDefinedGroup(getGroup(), event))
  // // {
  // // waitForNextGroupStart = true;
  // // }
  // }

  /**
   * Returns the page number (function value).
   *
   * @return the page number.
   */
  public Object getValue() {
    return IntegerCache.getInteger( getPage() );
  }

  /**
   * A internal flag that defines wether the next group start should be ignored. We have to ignore the first group start
   * of each report so that the report-header becomes part of the first group's page sequence.
   *
   * @return the internal flag.
   */
  protected boolean isIgnoreNextGroupStart() {
    return ignoreNextGroupStart;
  }

  /**
   * A obsolete getter. Ignore it please, it has no effect.
   *
   * @return true, if you care to know.
   * @deprecated No longer used.
   */
  protected boolean isIgnorePageCancelEvents() {
    return true;
  }

  /**
   * A obsolete setter. Ignore it please, it has no effect.
   *
   * @param ignorePageCancelEvents
   *          ignored.
   * @deprecated No longer used.
   */
  public void setIgnorePageCancelEvents( final boolean ignorePageCancelEvents ) {
  }
}
