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


package org.pentaho.reporting.libraries.base.util;

import java.io.Closeable;

public class StopWatch implements Closeable {
  private long elapsedTime;
  private long startTime;
  private boolean started;

  public StopWatch() {
  }

  public static StopWatch startNew() {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    return stopWatch;
  }

  public void start() {
    if ( started ) {
      return;
    }

    startTime = System.nanoTime();
    started = true;
  }

  public void reset() {
    elapsedTime = 0;
    startTime = System.nanoTime();
  }

  public void stop() {
    if ( started == false ) {
      return;
    }

    started = false;
    elapsedTime += ( System.nanoTime() - startTime );
  }

  public long getElapsedTime() {
    if ( started ) {
      return elapsedTime + ( System.nanoTime() - startTime );
    }
    return elapsedTime;
  }

  public double getElapsedMilliseconds() {
    return getElapsedTime() / 1000000.0f;
  }

  public double getElapsedSeconds() {
    return getElapsedTime() / 1000000000.0f;
  }

  public String toString() {
    return "StopWatch={elapsedTimeInSeconds=" + getElapsedSeconds() + "}";
  }

  public long getStartTime() {
    return startTime;
  }

  public double getStartMilliseconds() {
    return getStartTime() / 1000000.0f;
  }

  public boolean isStarted() {
    return started;
  }

  public void close() {
    stop();
  }
}
