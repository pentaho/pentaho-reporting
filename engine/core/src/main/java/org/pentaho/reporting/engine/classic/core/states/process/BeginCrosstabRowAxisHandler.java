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

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public class BeginCrosstabRowAxisHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new BeginCrosstabRowAxisHandler();

  private BeginCrosstabRowAxisHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.enterGroup();
    next.crosstabResetColumnIndices();
    next.fireReportEvent();
    next.enterPresentationGroup();
    return next;
  }

  public ProcessState commit( ProcessState next ) throws ReportProcessingException {
    final Group group = next.getReport().getGroup( next.getCurrentGroupIndex() );

    final GroupBody body = group.getBody();
    if ( body instanceof CrosstabRowGroupBody ) {
      next.setAdvanceHandler( BeginCrosstabRowAxisHandler.HANDLER );
    } else if ( body instanceof CrosstabColumnGroupBody ) {
      next = next.recordCrosstabRowState();
      next.setAdvanceHandler( BeginCrosstabColumnAxisHandler.HANDLER );
    } else {
      throw new IllegalStateException( "This report is totally messed up!" );
    }

    return next;

  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.CROSSTABBING_ROW | ReportEvent.GROUP_STARTED;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
