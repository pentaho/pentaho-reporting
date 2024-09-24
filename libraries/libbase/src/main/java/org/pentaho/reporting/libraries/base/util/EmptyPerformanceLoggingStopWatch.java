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

package org.pentaho.reporting.libraries.base.util;

public class EmptyPerformanceLoggingStopWatch implements PerformanceLoggingStopWatch {
  public static final PerformanceLoggingStopWatch INSTANCE = new EmptyPerformanceLoggingStopWatch();

  private EmptyPerformanceLoggingStopWatch() {
  }

  public long getLoggingThreshold() {
    return 0;
  }

  public void setLoggingThreshold( final long loggingThreshold ) {

  }

  public String getTag() {
    return null;
  }

  public Object getMessage() {
    return null;
  }

  public void setMessage( final Object message ) {

  }

  public void start() {

  }

  public void stop( final boolean pause ) {

  }

  public long getRestartCount() {
    return 0;
  }

  public void reset() {

  }

  public long getStartTime() {
    return 0;
  }

  public void stop() {

  }

  public void close() {

  }
}
