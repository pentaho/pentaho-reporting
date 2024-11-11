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
