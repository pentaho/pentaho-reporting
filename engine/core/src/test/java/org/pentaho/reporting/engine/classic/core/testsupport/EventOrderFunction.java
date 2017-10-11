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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;

public class EventOrderFunction extends AbstractFunction implements PageEventListener {
  private static final Log logger = LogFactory.getLog( EventOrderFunction.class );
  private int lastEventType;
  private boolean pageOpen;

  public EventOrderFunction() {
  }

  public EventOrderFunction( final String name ) {
    setName( name );
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <p/>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.REPORT_INITIALIZED ) != ReportEvent.REPORT_INITIALIZED ) {
      TestCase.fail( "ReportEvent was expected to be of type REPORT_INITIALIZED" );
    }

    if ( ( ( lastEventType & ReportEvent.REPORT_DONE ) != ReportEvent.REPORT_DONE ) && lastEventType != 0 ) {
      TestCase.fail( "Unexpected Event: ReportInitialized: " + lastEventType );
    }

    lastEventType = ReportEvent.REPORT_INITIALIZED;
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event
   *          the event.
   */
  public void reportStarted( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.REPORT_STARTED ) != ReportEvent.REPORT_STARTED ) {
      TestCase.fail( "ReportEvent was expected to be of type REPORT_STARTED" );
    }

    if ( ( lastEventType & ReportEvent.REPORT_INITIALIZED ) != ReportEvent.REPORT_INITIALIZED ) {
      TestCase.fail( "Unexpected Event: ReportStarted: " + lastEventType );
    }

    lastEventType = ReportEvent.REPORT_STARTED;
  }

  /**
   * Receives notification that the report has finished.
   *
   * @param event
   *          the event.
   */
  public void reportFinished( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.REPORT_FINISHED ) != ReportEvent.REPORT_FINISHED ) {
      TestCase.fail( "ReportEvent was expected to be of type REPORT_FINISHED" );
    }

    if ( ( lastEventType & ReportEvent.GROUP_FINISHED ) != ReportEvent.GROUP_FINISHED ) {
      TestCase.fail( "Unexpected Event: ReportFinished: " + lastEventType );
    }

    lastEventType = ReportEvent.REPORT_FINISHED;
  }

  /**
   * Receives notification that a page has started.
   *
   * @param event
   *          the event.
   */
  public void pageStarted( final ReportEvent event ) {
    logger.error( "! EventOrderFunction: Page Started called !" );
    if ( ( event.getType() & ReportEvent.PAGE_STARTED ) != ReportEvent.PAGE_STARTED ) {
      TestCase.fail( "ReportEvent was expected to be of type PAGE_STARTED" );
    }

    if ( pageOpen ) {
      TestCase.fail( "Unexpected Event: PageStarted: " + lastEventType );
    }
    pageOpen = true;
  }

  /**
   * Receives notification that a page has ended.
   *
   * @param event
   *          the event.
   */
  public void pageFinished( final ReportEvent event ) {
    logger.error( "! EventOrderFunction: Page Finished called !" );
    if ( ( event.getType() & ReportEvent.PAGE_FINISHED ) != ReportEvent.PAGE_FINISHED ) {
      TestCase.fail( "ReportEvent was expected to be of type PAGE_FINISHED: " + event.getType() );
    }

    if ( pageOpen == false ) {
      TestCase.fail( "Unexpected Event: PageFinished: " + lastEventType );
    }
    pageOpen = false;
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    logger.error( "! EventOrderFunction: Group Started called !" );
    if ( ( event.getType() & ReportEvent.GROUP_STARTED ) != ReportEvent.GROUP_STARTED ) {
      TestCase.fail( "ReportEvent was expected to be of type GROUP_STARTED" );
    }
    logger.error( "! EventOrderFunction: Group Started called !" );

    if ( ( lastEventType & ReportEvent.GROUP_STARTED ) != ReportEvent.GROUP_STARTED
        && ( lastEventType & ReportEvent.REPORT_STARTED ) != ReportEvent.REPORT_STARTED ) {
      logger.error( " ++! EventOrderFunction: Group Started called !", new Exception() );
      TestCase.fail( "Unexpected Event: GroupStarted: " + lastEventType );
    }
    logger.error( "! EventOrderFunction: Group Started called !" );

    lastEventType = ReportEvent.GROUP_STARTED;
    logger.error( "! EventOrderFunction: Group Started called !" );
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.GROUP_FINISHED ) != ReportEvent.GROUP_FINISHED ) {
      TestCase.fail( "ReportEvent was expected to be of type GROUP_FINISHED" );
    }

    if ( ( lastEventType & ReportEvent.GROUP_FINISHED ) != ReportEvent.GROUP_FINISHED
        && ( lastEventType & ReportEvent.ITEMS_FINISHED ) != ReportEvent.ITEMS_FINISHED ) {
      TestCase.fail( "Unexpected Event: GroupFinished: " + lastEventType );
    }

    lastEventType = ReportEvent.GROUP_FINISHED;
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.ITEMS_ADVANCED ) != ReportEvent.ITEMS_ADVANCED ) {
      TestCase.fail( "ReportEvent was expected to be of type ITEMS_ADVANCED" );
    }

    if ( ( lastEventType & ReportEvent.ITEMS_STARTED ) != ReportEvent.ITEMS_STARTED
        && ( lastEventType & ReportEvent.ITEMS_ADVANCED ) != ReportEvent.ITEMS_ADVANCED ) {
      TestCase.fail( "Unexpected Event: ReportDone: " + lastEventType );
    }

    lastEventType = ReportEvent.ITEMS_ADVANCED;

  }

  /**
   * Receives notification that a group of item bands is about to be processed.
   * <p/>
   * The next events will be itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event
   *          The event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.ITEMS_STARTED ) != ReportEvent.ITEMS_STARTED ) {
      TestCase.fail( "ReportEvent was expected to be of type ITEMS_STARTED" );
    }

    if ( ( lastEventType & ReportEvent.GROUP_STARTED ) != ReportEvent.GROUP_STARTED ) {
      TestCase.fail( "Unexpected Event: ItemsStarted: " + lastEventType );
    }

    lastEventType = ReportEvent.ITEMS_STARTED;

  }

  /**
   * Receives notification that a group of item bands has been completed.
   * <p/>
   * The itemBand is finished, the report starts to close open groups.
   *
   * @param event
   *          The event.
   */
  public void itemsFinished( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.ITEMS_FINISHED ) != ReportEvent.ITEMS_FINISHED ) {
      TestCase.fail( "ReportEvent was expected to be of type ITEMS_FINISHED" );
    }

    if ( ( lastEventType & ReportEvent.ITEMS_ADVANCED ) != ReportEvent.ITEMS_ADVANCED ) {
      TestCase.fail( "Unexpected Event: ItemsFinished: " + lastEventType );
    }

    lastEventType = ReportEvent.ITEMS_FINISHED;
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event
   *          The event.
   */
  public void reportDone( final ReportEvent event ) {
    if ( ( event.getType() & ReportEvent.REPORT_DONE ) != ReportEvent.REPORT_DONE ) {
      TestCase.fail( "ReportEvent was expected to be of type REPORT_DONE" );
    }

    if ( ( lastEventType & ReportEvent.REPORT_FINISHED ) != ReportEvent.REPORT_FINISHED ) {
      TestCase.fail( "Unexpected Event: ReportDone: " + lastEventType );
    }

    lastEventType = ReportEvent.REPORT_DONE;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return new Integer( lastEventType );
  }
}
