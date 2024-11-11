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


import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;

public interface IAsyncReportListener extends ReportProgressListener {

  void setStatus( AsyncExecutionStatus status );

  boolean isFirstPageMode();

  int getRequestedPage();

  void updateGenerationStatus( int generatedPage );

  void setErrorMessage( String errorMessage );

  boolean isScheduled();

  boolean isQueryLimitReached();

  void setIsQueryLimitReached( boolean isQueryLimitReached );

  default int getTotalRows() {
    return 0;
  }

  void subscribeToReportCancelEvent( IReportCancelEvent reportCancelEvent );

  void notifyReportCancelEventToSubscribers();
}
