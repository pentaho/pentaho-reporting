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

import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class ProcessDetailsHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new ProcessDetailsHandler();

  private ProcessDetailsHandler() {
  }

  public int getEventCode() {
    return ReportEvent.ITEMS_ADVANCED;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();

    final ItemBand childs = next.getReport().getItemBand();
    if ( childs != null ) {
      return InlineSubreportProcessor.processInline( next, childs );
    }
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final ItemBand childs = next.getReport().getItemBand();
    if ( childs == null ) {
      return JoinDetailsHandler.HANDLER.commit( next );
    }

    if ( InlineSubreportProcessor.hasSubReports( next, childs ) ) {
      next.setAdvanceHandler( JoinDetailsHandler.HANDLER );
      return InlineSubreportProcessor.processBandedSubReports( next, childs );
    }

    return JoinDetailsHandler.HANDLER.commit( next );
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
