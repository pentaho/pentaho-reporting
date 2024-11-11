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


package org.pentaho.reporting.engine.classic.core.event.async;

public class ReportListenerThreadHolder {

  private static final ThreadLocal<IAsyncReportListener> listenerThreadLocal =
    new ThreadLocal<>();
  private static final ThreadLocal<String> auditIdLocal = new ThreadLocal<>();

  public static IAsyncReportListener getListener() {
    return listenerThreadLocal.get();
  }

  public static void setListener( final IAsyncReportListener listener ) {
    listenerThreadLocal.set( listener );
  }

  public static void setRequestId( final String requestId ) {
    auditIdLocal.set( requestId );
  }

  public static String getRequestId() {
    return auditIdLocal.get();
  }

  public static void clear() {
    listenerThreadLocal.remove();
    auditIdLocal.remove();
  }

}
