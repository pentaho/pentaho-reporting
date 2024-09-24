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

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class JoinCrosstabFactHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new JoinCrosstabFactHandler();

  public JoinCrosstabFactHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final DefaultFlowController fc = next.getFlowController().performAdvance();
    final Group group = next.getReport().getGroup( next.getCurrentGroupIndex() );
    final DefaultFlowController cfc = fc.performCommit();
    if ( ProcessState.isLastItemInGroup( group, fc.getMasterRow(), cfc.getMasterRow() ) ) {
      next.setFlowController( fc );
      next.setAdvanceHandler( EndCrosstabFactHandler.HANDLER );
    } else {
      next.setFlowController( cfc );
      next.setAdvanceHandler( ProcessCrosstabFactHandler.HANDLER );
    }
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.ITEMS_ADVANCED | ProcessState.ARTIFICIAL_EVENT_CODE | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
