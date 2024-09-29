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

import java.io.Closeable;

public interface PerformanceLoggingStopWatch extends Closeable {
  long getLoggingThreshold();

  void setLoggingThreshold( long loggingThreshold );

  String getTag();

  Object getMessage();

  void setMessage( Object message );

  void start();

  void stop( boolean pause );

  long getRestartCount();

  void reset();

  long getStartTime();

  void stop();

  void close();
}
