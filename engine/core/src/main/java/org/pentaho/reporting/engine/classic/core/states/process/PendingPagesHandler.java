/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

/**
 * This handler deferrs the event progression by one "advance" call, so that we can hopefully clean up the pages and
 * generate some page-events.
 *
 * @author Thomas Morgner.
 */
public class PendingPagesHandler implements AdvanceHandler {
  private AdvanceHandler handler;

  public PendingPagesHandler( final AdvanceHandler handler ) {
    if ( handler == null ) {
      throw new NullPointerException();
    }
    this.handler = handler;
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    final ProcessState newState = state.deriveForAdvance();
    newState.getFlowController().getMasterRow().refresh();
    newState.getLayoutProcess().restart( newState );
    return newState;
  }

  public ProcessState commit( final ProcessState state ) throws ReportProcessingException {
    final ProcessState nstate = state.deriveForAdvance();
    nstate.setAdvanceHandler( handler );
    return handler.commit( nstate );
  }

  public boolean isFinish() {
    return handler.isFinish();
  }

  public int getEventCode() {
    return handler.getEventCode();
  }

  public static ProcessState create( final ProcessState state ) {
    if ( state.getAdvanceHandler() instanceof PendingPagesHandler ) {
      return state.deriveForAdvance();
    }
    final ProcessState newstate = state.deriveForAdvance();
    newstate.setAdvanceHandler( new PendingPagesHandler( newstate.getAdvanceHandler() ) );
    return newstate;
  }

  public boolean isRestoreHandler() {
    return true;
  }
}
