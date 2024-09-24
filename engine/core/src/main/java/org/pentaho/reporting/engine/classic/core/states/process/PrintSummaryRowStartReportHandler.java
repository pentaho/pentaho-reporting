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

public class PrintSummaryRowStartReportHandler implements AdvanceHandler {
  public static final PrintSummaryRowStartReportHandler HANDLER = new PrintSummaryRowStartReportHandler();

  public PrintSummaryRowStartReportHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.replayStoredCrosstabRowState();
    next.crosstabResetColumnIndices();
    next.setAdvanceHandler( PrintSummaryRowFireEventReportHandler.HANDLER );
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.CROSSTABBING | ReportEvent.SUMMARY_ROW_START | ReportEvent.ARTIFICIAL_EVENT_CODE;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
