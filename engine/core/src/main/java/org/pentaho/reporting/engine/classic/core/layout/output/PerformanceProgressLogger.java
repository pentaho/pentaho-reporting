/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.libraries.base.util.MemoryUsageMessage;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

public class PerformanceProgressLogger implements ReportProgressListener {
  private static final Log logger = LogFactory.getLog( PerformanceProgressLogger.class );
  private static final int ROW_PROGRESS = 5000;
  private int lastPage;
  private int lastRow;
  private int lastStage;
  private int lastActivity;
  private long startTime;
  private int rowCount;

  private boolean logPageProgress;
  private boolean logLevelProgress;
  private boolean logRowProgress;

  public PerformanceProgressLogger() {
    this( true, true, true );
  }

  public PerformanceProgressLogger( final boolean logLevelProgress, final boolean logPageProgress,
      final boolean logRowProgress ) {
    this.logLevelProgress = logLevelProgress;
    this.logPageProgress = logPageProgress;
    this.logRowProgress = logRowProgress;
  }

  /**
   * Receives a notification that the report processing has started.
   *
   * @param event
   *          the start event.
   */
  public void reportProcessingStarted( final ReportProgressEvent event ) {
    if ( logger.isInfoEnabled() == false ) {
      return;
    }

    rowCount = -1;
    startTime = System.currentTimeMillis();
    logger.info( new MemoryUsageMessage( "[" + Thread.currentThread().getName() + "] Report Processing started. " ) );
  }

  /**
   * Receives a notification that the report processing made some progress.
   *
   * @param event
   *          the update event.
   */
  public void reportProcessingUpdate( final ReportProgressEvent event ) {
    if ( logger.isInfoEnabled() == false ) {
      return;
    }

    rowCount = event.getMaximumRow();
    boolean print = false;
    if ( lastStage != event.getLevel() || lastActivity != event.getActivity() ) {
      lastStage = event.getLevel();
      lastActivity = event.getActivity();
      lastRow = 0;
      if ( logLevelProgress ) {
        print = true;
      }
    }
    if ( lastPage != event.getPage() ) {
      lastPage = event.getPage();
      if ( logPageProgress ) {
        print = true;
      }
    }
    final int modRow = ( event.getRow() - lastRow );
    if ( modRow > ROW_PROGRESS ) {
      lastRow = ( event.getRow() / ROW_PROGRESS ) * ROW_PROGRESS;
      if ( logRowProgress ) {
        print = true;
      }
    }

    if ( print ) {
      logger.info( new MemoryUsageMessage( "[" + Thread.currentThread().getName() + "] Activity: "
          + event.getActivity() + " Level: " + +lastStage + " Processing page: " + lastPage + " Row: " + lastRow
          + " Time: " + ( System.currentTimeMillis() - startTime ) + "ms " ) );

    }
  }

  /**
   * Receives a notification that the report processing was finished.
   *
   * @param event
   *          the finish event.
   */
  public void reportProcessingFinished( final ReportProgressEvent event ) {
    if ( logger.isInfoEnabled() == false ) {
      return;
    }

    final FastMessageFormat messageFormat =
        new FastMessageFormat( "[{0}] Report Processing Finished: {1}ms - {2,number,0.000} rows/sec - " );

    final long processTime = System.currentTimeMillis() - startTime;
    final double rowsPerSecond = rowCount * 1000.0f / ( processTime );
    logger.info( new MemoryUsageMessage( messageFormat.format( new Object[] { Thread.currentThread().getName(),
      new Long( processTime ), new Double( rowsPerSecond ) } ) ) );

  }
}
