/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;

/**
 * Creation-Date: 03.07.2007, 13:05:16
 *
 * @author Thomas Morgner
 */
public class BeginReportHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new BeginReportHandler();

  private BeginReportHandler() {
  }

  public int getEventCode() {
    return ReportEvent.REPORT_INITIALIZED;
  }

  public boolean isFinish() {
    return false;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    if ( !state.getFlowController().getMasterRow().getExpressionDataRow().isValid() ) {
      throw new IllegalStateException( "The expression data-row must be valid upon the start of the report processing." );
    }

    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();
    if ( next.isSubReportEvent() && next.getLevel() == LayoutProcess.LEVEL_PAGINATE ) {
      next.firePageStartedEvent( ReportEvent.REPORT_INITIALIZED | ReportEvent.NO_PARENT_PASSING_EVENT );
    }
    return next;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    if ( next.isSubReportExecutable() ) {
      next.setAdvanceHandler( ReportHeaderHandler.HANDLER );
    } else {
      next.setAdvanceHandler( ReportDoneHandler.HANDLER );
    }
    return next;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
