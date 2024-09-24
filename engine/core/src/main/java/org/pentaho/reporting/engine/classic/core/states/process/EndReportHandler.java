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

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class EndReportHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new EndReportHandler();

  public EndReportHandler() {
  }

  public int getEventCode() {
    return ReportEvent.REPORT_DONE | ProcessState.ARTIFICIAL_EVENT_CODE;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    throw new ReportProcessingException( "Cannot advance beyond finish!" );
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    throw new ReportProcessingException( "Cannot advance beyond finish!" );
  }

  public boolean isFinish() {
    return true;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
