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
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class ReportFooterHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new ReportFooterHandler();

  private ReportFooterHandler() {
  }

  public int getEventCode() {
    return ReportEvent.REPORT_FINISHED;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    return InlineSubreportProcessor.processInline( next, next.getReport().getReportFooter() );
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.setAdvanceHandler( ReportDoneHandler.HANDLER );

    final RootLevelBand rootLevelBand = next.getReport().getReportFooter();

    return InlineSubreportProcessor.processBandedSubReports( next, rootLevelBand );
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
