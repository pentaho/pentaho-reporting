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
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;

/**
 * Creation-Date: 04.07.2007, 19:00:19
 *
 * @author Thomas Morgner
 */
public class EndSubReportHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new EndSubReportHandler();

  private EndSubReportHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    final InlineSubreportMarker[] subReports = state.getSubReports();
    final int currentSubReport = state.getCurrentSubReport();

    final int nextIndex =
        InlineSubreportProcessor.findNextIndex( subReports, state.getSubreportProcessingType(), currentSubReport + 1 );
    if ( nextIndex != -1 ) {
      final ProcessState parentState = (ProcessState) state.getParentSubReportState();
      final ProcessState parentNext = parentState.returnFromSubReport( state.getLayoutProcess().getParent() );
      parentNext.setFlowController( state.getFlowController() );
      parentNext.setSequenceCounter( state.getSequenceCounter() + 1 );

      final ProcessState processState = new ProcessState();
      processState.initializeForSubreport( subReports, nextIndex, parentNext );
      return processState;
    } else {
      // No more sub-reports, so join back with the parent ..
      final ProcessState parentState = (ProcessState) state.getParentSubReportState();
      final ProcessState parentNext = parentState.returnFromSubReport( state.getLayoutProcess().getParent() );
      parentNext.setFlowController( state.getFlowController() );
      parentNext.setSequenceCounter( state.getSequenceCounter() + 1 );
      return parentNext;
    }
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.REPORT_DONE | ProcessState.ARTIFICIAL_EVENT_CODE;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
