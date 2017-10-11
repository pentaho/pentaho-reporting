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

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

/**
 * This delays the actual test on whether the current detail-group should be finished until the subreports have been
 * processed. The subreports can influence this test by declaring output-parameters.
 *
 * @author Thomas Morgner
 */
public class JoinDetailsHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new JoinDetailsHandler();

  private JoinDetailsHandler() {
  }

  public int getEventCode() {
    return ReportEvent.ITEMS_ADVANCED | ProcessState.ARTIFICIAL_EVENT_CODE;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    final Group group = next.getReport().getGroup( next.getCurrentGroupIndex() );

    next.advanceCursor();
    final DefaultFlowController fc = next.getFlowController().performAdvance();
    final DefaultFlowController cfc = fc.performCommit();
    if ( ProcessState.isLastItemInGroup( group, fc.getMasterRow(), cfc.getMasterRow() ) ) {
      next.setFlowController( fc );
      next.setAdvanceHandler( EndDetailsHandler.HANDLER );
    } else {
      next.setFlowController( cfc );
      next.setAdvanceHandler( ProcessDetailsHandler.HANDLER );
    }
    return next;
  }

  public boolean isFinish() {
    return false;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
