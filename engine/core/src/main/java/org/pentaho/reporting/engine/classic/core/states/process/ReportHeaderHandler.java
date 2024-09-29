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

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creation-Date: 03.07.2007, 13:36:56
 *
 * @author Thomas Morgner
 */
public class ReportHeaderHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new ReportHeaderHandler();

  private ReportHeaderHandler() {
  }

  public int getEventCode() {
    return ReportEvent.REPORT_STARTED;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    return InlineSubreportProcessor.processInline( next, next.getReport().getReportHeader() );
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final Group rootGroup = next.getReport().getGroup( 0 );
    if ( rootGroup instanceof CrosstabGroup ) {
      next.setAdvanceHandler( BeginCrosstabHandler.HANDLER );
    } else {
      next.setAdvanceHandler( BeginGroupHandler.HANDLER );
    }

    final RootLevelBand rootLevelBand = next.getReport().getReportHeader();
    return InlineSubreportProcessor.processBandedSubReports( next, rootLevelBand );
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
