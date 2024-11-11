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


package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

import javax.swing.event.ChangeListener;

public interface PerformanceMonitorContext {
  public PerformanceLoggingStopWatch createStopWatch( String tag );

  public PerformanceLoggingStopWatch createStopWatch( String tag, Object message );

  public void addChangeListener( ChangeListener listener );

  public void removeChangeListener( ChangeListener listener );

  public void close();
}
