/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 2 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2024 Hitachi Vantara.  All rights reserved.
 */

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
