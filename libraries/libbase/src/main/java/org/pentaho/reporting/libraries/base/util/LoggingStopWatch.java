/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingStopWatch extends StopWatch implements PerformanceLoggingStopWatch {
  private transient Log logger;
  private Object message;
  private String tag;
  private long loggingThreshold;
  private long firstStartTime;
  private long restartCount;

  public LoggingStopWatch( final String tag ) {
    ArgumentNullException.validate( "tag", tag );

    this.tag = tag;
  }

  public LoggingStopWatch( final String tag, final Object message ) {
    this( tag );
    this.message = message;
  }

  public static PerformanceLoggingStopWatch startNew( final String tag, final Object message ) {
    PerformanceLoggingStopWatch loggingStopWatch = new LoggingStopWatch( tag, message );
    loggingStopWatch.start();
    return loggingStopWatch;
  }

  public static PerformanceLoggingStopWatch startNew( final String tag, final String pattern,
                                                      final Object... message ) {
    return startNew( tag, new FormattedMessage( pattern, message ) );
  }

  public static PerformanceLoggingStopWatch startNew( final String tag ) {
    return startNew( tag, null );
  }

  public long getLoggingThreshold() {
    return loggingThreshold;
  }

  public void setLoggingThreshold( final long loggingThreshold ) {
    this.loggingThreshold = loggingThreshold;
  }

  public String getTag() {
    return tag;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage( final Object message ) {
    this.message = message;
  }

  public void start() {
    if ( isStarted() ) {
      return;
    }

    super.start();
    if ( firstStartTime == 0 ) {
      firstStartTime = super.getStartTime();
    }
    restartCount += 1;
  }

  public void stop( boolean pause ) {
    super.stop();
    if ( pause ) {
      return;
    }

    if ( getElapsedMilliseconds() < loggingThreshold ) {
      return;
    }

    if ( firstStartTime == 0 ) {
      // this stopwatch was never started ..
      return;
    }

    String logMessage;
    if ( message == null ) {
      logMessage = String.format( "start[%d] time[%d] tag[%s] count[%d]", getStartTime(), getElapsedTime(), getTag(),
        getRestartCount() );
    } else {
      logMessage = String
        .format( "start[%d] time[%d] tag[%s] count[%d] message[%s]", getStartTime(), getElapsedTime(), getTag(),
          getRestartCount(), getMessage() );
    }
    doLog( logMessage );
    reset();
  }

  public long getRestartCount() {
    return restartCount;
  }

  public void reset() {
    super.reset();
    firstStartTime = 0;
  }

  public long getStartTime() {
    return firstStartTime;
  }

  public void stop() {
    stop( false );
  }

  protected void doLog( String message ) {
    if ( logger == null ) {
      // no need to syncronize, if logger is null in a race-condition, the logging system will sort it out.
      logger = LogFactory.getLog( LoggingStopWatch.class.getName() + "." + tag );
    }
    if ( logger.isInfoEnabled() ) {
      logger.debug( message );
    }
  }
}
