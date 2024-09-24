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

public class BeginCrosstabFactHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new BeginCrosstabFactHandler();

  private BeginCrosstabFactHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.setInItemGroup( true );
    next.setAdvanceHandler( ProcessCrosstabFactHandler.HANDLER );
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.ITEMS_STARTED | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
