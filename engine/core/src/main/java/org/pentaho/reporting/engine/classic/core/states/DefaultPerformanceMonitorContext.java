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

import org.pentaho.reporting.libraries.base.util.LoggingStopWatch;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

import javax.swing.event.ChangeListener;

public class DefaultPerformanceMonitorContext implements PerformanceMonitorContext {
  public PerformanceLoggingStopWatch createStopWatch( final String tag ) {
    return new LoggingStopWatch( tag );
  }

  public PerformanceLoggingStopWatch createStopWatch( final String tag, final Object message ) {
    return new LoggingStopWatch( tag, message );
  }

  public void addChangeListener( final ChangeListener listener ) {

  }

  public void removeChangeListener( final ChangeListener listener ) {

  }

  public void close() {

  }
}
