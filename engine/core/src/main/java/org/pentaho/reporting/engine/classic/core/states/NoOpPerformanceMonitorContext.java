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


package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.libraries.base.util.EmptyPerformanceLoggingStopWatch;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

import javax.swing.event.ChangeListener;

public class NoOpPerformanceMonitorContext implements PerformanceMonitorContext {
  public PerformanceLoggingStopWatch createStopWatch( final String tag ) {
    return EmptyPerformanceLoggingStopWatch.INSTANCE;
  }

  public PerformanceLoggingStopWatch createStopWatch( final String tag, final Object message ) {
    return EmptyPerformanceLoggingStopWatch.INSTANCE;
  }

  public void addChangeListener( final ChangeListener listener ) {

  }

  public void removeChangeListener( final ChangeListener listener ) {

  }

  public void close() {

  }
}
