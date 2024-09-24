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

import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public class BeginCrosstabOtherAxisHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new BeginCrosstabOtherAxisHandler();

  private BeginCrosstabOtherAxisHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.enterGroup();
    next.fireReportEvent();
    next.enterPresentationGroup();
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final Group group = next.getReport().getGroup( next.getCurrentGroupIndex() );

    final GroupBody body = group.getBody();
    if ( body instanceof CrosstabRowGroupBody ) {
      next.setAdvanceHandler( BeginCrosstabRowAxisHandler.HANDLER );
    } else if ( body instanceof CrosstabOtherGroupBody ) {
      next.setAdvanceHandler( BeginCrosstabOtherAxisHandler.HANDLER );
    } else {
      throw new IllegalStateException( "This report is totally messed up!" );
    }
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.CROSSTABBING_OTHER | ReportEvent.GROUP_STARTED;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
