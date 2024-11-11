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


package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public class PrintSummaryEndCrosstabColumnAxisHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new PrintSummaryEndCrosstabColumnAxisHandler();

  public PrintSummaryEndCrosstabColumnAxisHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.leavePresentationGroup();
    final Group group = next.getReport().getGroup( state.getCurrentGroupIndex() );
    if ( group instanceof CrosstabColumnGroup ) {
      next.fireReportEvent();
    }
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.setAdvanceHandler( PrintSummaryJoinEndCrosstabColumnAxisHandler.HANDLER );
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.SUMMARY_ROW | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
