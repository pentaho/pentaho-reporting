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
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;

/**
 * Creation-Date: 04.07.2007, 19:00:19
 *
 * @author Thomas Morgner
 */
public class EndSubReportHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new EndSubReportHandler();

  private EndSubReportHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    final InlineSubreportMarker[] subReports = state.getSubReports();
    final int currentSubReport = state.getCurrentSubReport();

    final int nextIndex =
        InlineSubreportProcessor.findNextIndex( subReports, state.getSubreportProcessingType(), currentSubReport + 1 );
    if ( nextIndex != -1 ) {
      final ProcessState parentState = (ProcessState) state.getParentSubReportState();
      final ProcessState parentNext = parentState.returnFromSubReport( state.getLayoutProcess().getParent() );
      parentNext.setFlowController( state.getFlowController() );
      parentNext.setSequenceCounter( state.getSequenceCounter() + 1 );

      final ProcessState processState = new ProcessState();
      processState.initializeForSubreport( subReports, nextIndex, parentNext );
      return processState;
    } else {
      // No more sub-reports, so join back with the parent ..
      final ProcessState parentState = (ProcessState) state.getParentSubReportState();
      final ProcessState parentNext = parentState.returnFromSubReport( state.getLayoutProcess().getParent() );
      parentNext.setFlowController( state.getFlowController() );
      parentNext.setSequenceCounter( state.getSequenceCounter() + 1 );
      return parentNext;
    }
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.REPORT_DONE | ProcessState.ARTIFICIAL_EVENT_CODE;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
