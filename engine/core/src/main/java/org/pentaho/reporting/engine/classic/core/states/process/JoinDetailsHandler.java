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

/**
 * This delays the actual test on whether the current detail-group should be finished until the subreports have been
 * processed. The subreports can influence this test by declaring output-parameters.
 *
 * @author Thomas Morgner
 */
public class JoinDetailsHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new JoinDetailsHandler();

  private JoinDetailsHandler() {
  }

  public int getEventCode() {
    return ReportEvent.ITEMS_ADVANCED | ProcessState.ARTIFICIAL_EVENT_CODE;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final Group group = next.getReport().getGroup( next.getCurrentGroupIndex() );

    next.advanceCursor();
    final DefaultFlowController fc = next.getFlowController().performAdvance();
    final DefaultFlowController cfc = fc.performCommit();
    if ( ProcessState.isLastItemInGroup( group, fc.getMasterRow(), cfc.getMasterRow() ) ) {
      next.setFlowController( fc );
      next.setAdvanceHandler( EndDetailsHandler.HANDLER );
    } else {
      next.setFlowController( cfc );
      next.setAdvanceHandler( ProcessDetailsHandler.HANDLER );
    }
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
