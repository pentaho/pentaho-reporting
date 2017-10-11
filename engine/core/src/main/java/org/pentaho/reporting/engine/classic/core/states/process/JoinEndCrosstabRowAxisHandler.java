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

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class JoinEndCrosstabRowAxisHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new JoinEndCrosstabRowAxisHandler();

  public JoinEndCrosstabRowAxisHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  /**
   * Checks whether there are more groups active.
   *
   * @return true if this is the last (outer-most) group.
   */
  private boolean isRootGroup( final ProcessState state ) {
    return state.getCurrentGroupIndex() == ReportState.BEFORE_FIRST_GROUP;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.leaveGroup();
    final DefaultFlowController fc = next.getFlowController();
    final boolean advanceRequested = fc.isAdvanceRequested();
    final boolean advanceable = fc.getMasterRow().isAdvanceable();
    if ( isRootGroup( next ) ) {
      throw new ReportProcessingException( "This report is invalid. A CR-Row-Group cannot be a root group." );
    }

    final Group parentGroup = next.getReport().getGroup( next.getCurrentGroupIndex() );
    if ( advanceRequested == false || advanceable == false ) {
      // This happens for empty - reports. Empty-Reports are never advanceable, therefore we can
      // reach an non-advance state where inner group-footers are printed.
      if ( parentGroup instanceof CrosstabRowGroup ) {
        next.setPostSummaryRowAdvanceHandler( EndCrosstabRowBodyHandler.HANDLER );
      } else if ( parentGroup instanceof CrosstabGroup ) {
        next.setPostSummaryRowAdvanceHandler( EndCrosstabBodyHandler.HANDLER );
      } else if ( parentGroup instanceof CrosstabOtherGroup ) {
        next.setPostSummaryRowAdvanceHandler( EndCrosstabOtherBodyHandler.HANDLER );
      } else {
        throw new ReportProcessingException( "This report is invalid." );
      }
      next.setAdvanceHandler( PrintSummaryRowStartReportHandler.HANDLER );
      return next;
    }

    // This group is not the outer-most group ..
    final DefaultFlowController cfc = fc.performCommit();
    if ( ProcessState.isLastItemInGroup( parentGroup, fc.getMasterRow(), cfc.getMasterRow() ) ) {
      // print summary group ..

      // continue with an other EndGroup-State ...
      if ( parentGroup instanceof CrosstabRowGroup ) {
        next.setPostSummaryRowAdvanceHandler( EndCrosstabRowBodyHandler.HANDLER );
      } else if ( parentGroup instanceof CrosstabGroup ) {
        next.setPostSummaryRowAdvanceHandler( EndCrosstabBodyHandler.HANDLER );
      } else if ( parentGroup instanceof CrosstabOtherGroup ) {
        next.setPostSummaryRowAdvanceHandler( EndCrosstabOtherBodyHandler.HANDLER );
      } else {
        throw new ReportProcessingException( "This report is invalid." );
      }
      next.setAdvanceHandler( PrintSummaryRowStartReportHandler.HANDLER );
      return next;
    } else {
      // The parent group is not finished, so finalize the createRollbackInformation.
      // more data in parent group, print the next header
      final DefaultFlowController rfc = cfc.resetRowCursor();
      next.setFlowController( rfc );
      next.setAdvanceHandler( BeginCrosstabRowAxisHandler.HANDLER );
      return next;
    }
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.GROUP_FINISHED | ProcessState.ARTIFICIAL_EVENT_CODE | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
