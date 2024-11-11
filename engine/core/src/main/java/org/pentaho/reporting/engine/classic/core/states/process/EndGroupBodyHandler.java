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

public class EndGroupBodyHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new EndGroupBodyHandler();

  public EndGroupBodyHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.leavePresentationGroup();
    next.fireReportEvent();
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.setAdvanceHandler( EndGroupHandler.HANDLER );
    return next;
  }

  public int getEventCode() {
    return ReportEvent.GROUP_BODY_FINISHED;
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
