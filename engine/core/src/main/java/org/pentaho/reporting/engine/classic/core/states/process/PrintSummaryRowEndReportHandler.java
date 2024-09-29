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


package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public class PrintSummaryRowEndReportHandler implements AdvanceHandler {
  public static final PrintSummaryRowEndReportHandler HANDLER = new PrintSummaryRowEndReportHandler();

  public PrintSummaryRowEndReportHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    return next;
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.finishReplayingStoredCrosstabRowState();
    next.setAdvanceHandler( state.getPostSummaryRowAdvanceHandler() );
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.CROSSTABBING | ReportEvent.SUMMARY_ROW_END;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
