/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

/**
 * Creation-Date: 04.07.2007, 14:01:43
 *
 * @author Thomas Morgner
 */
public class IgnoreEverythingReportErrorHandler implements ReportProcessingErrorHandler {
  public static final ReportProcessingErrorHandler INSTANCE = new IgnoreEverythingReportErrorHandler();

  private static final Exception[] EMPTY_EXCEPTION = new Exception[0];

  private IgnoreEverythingReportErrorHandler() {
  }

  public void handleError( final Exception exception ) {

  }

  public boolean isErrorOccured() {
    return false;
  }

  public Exception[] getErrors() {
    return IgnoreEverythingReportErrorHandler.EMPTY_EXCEPTION;
  }

  public void clearErrors() {

  }
}
