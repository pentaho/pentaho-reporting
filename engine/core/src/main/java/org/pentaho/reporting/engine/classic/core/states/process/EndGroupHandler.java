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

import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class EndGroupHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new EndGroupHandler();

  public EndGroupHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    final RelationalGroup group = (RelationalGroup) next.getReport().getGroup( next.getCurrentGroupIndex() );
    return InlineSubreportProcessor.processInline( next, group.getFooter() );
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.setAdvanceHandler( JoinEndGroupHandler.HANDLER );

    final RelationalGroup group = (RelationalGroup) next.getReport().getGroup( next.getCurrentGroupIndex() );
    final RootLevelBand rootLevelBand = group.getFooter();
    return InlineSubreportProcessor.processBandedSubReports( next, rootLevelBand );
  }

  public int getEventCode() {
    return ReportEvent.GROUP_FINISHED;
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
