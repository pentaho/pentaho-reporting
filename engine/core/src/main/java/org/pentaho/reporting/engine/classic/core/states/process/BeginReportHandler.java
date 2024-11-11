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

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;

/**
 * Creation-Date: 03.07.2007, 13:05:16
 *
 * @author Thomas Morgner
 */
public class BeginReportHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new BeginReportHandler();

  private BeginReportHandler() {
  }

  public int getEventCode() {
    return ReportEvent.REPORT_INITIALIZED;
  }

  public boolean isFinish() {
    return false;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    if ( !state.getFlowController().getMasterRow().getExpressionDataRow().isValid() ) {
      throw new IllegalStateException( "The expression data-row must be valid upon the start of the report processing." );
    }

    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    if ( next.isSubReportEvent() && next.getLevel() == LayoutProcess.LEVEL_PAGINATE ) {
      next.firePageStartedEvent( ReportEvent.REPORT_INITIALIZED | ReportEvent.NO_PARENT_PASSING_EVENT );
    }
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    if ( next.isSubReportExecutable() ) {
      next.setAdvanceHandler( ReportHeaderHandler.HANDLER );
    } else {
      next.setAdvanceHandler( ReportDoneHandler.HANDLER );
    }
    return next;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
