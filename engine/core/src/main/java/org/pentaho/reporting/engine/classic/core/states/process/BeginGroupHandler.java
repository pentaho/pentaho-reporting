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

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class BeginGroupHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new BeginGroupHandler();

  private BeginGroupHandler() {
  }

  public int getEventCode() {
    return ReportEvent.GROUP_STARTED;
  }

  private boolean hasMoreGroups( final ProcessState state ) {
    return state.getCurrentGroupIndex() < ( state.getReport().getGroupCount() - 1 );
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.enterGroup();
    next.fireReportEvent();
    next.enterPresentationGroup();
    final RelationalGroup group = (RelationalGroup) next.getReport().getGroup( next.getCurrentGroupIndex() );
    return InlineSubreportProcessor.processInline( next, group.getHeader() );
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {

    if ( hasMoreGroups( next ) == false ) {
      next.setAdvanceHandler( BeginDetailsHandler.HANDLER );
    } else {
      // it is safe to query the next group instance here ...
      final Group nextGroup = next.getReport().getGroup( next.getCurrentGroupIndex() + 1 );
      if ( nextGroup instanceof CrosstabGroup ) {
        next.setAdvanceHandler( BeginCrosstabHandler.HANDLER );
      }
      // else stick with begin-group as there will be a next group to start ..
    }

    final RelationalGroup group = (RelationalGroup) next.getReport().getGroup( next.getCurrentGroupIndex() );
    final RootLevelBand rootLevelBand = group.getHeader();
    return InlineSubreportProcessor.processBandedSubReports( next, rootLevelBand );
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
