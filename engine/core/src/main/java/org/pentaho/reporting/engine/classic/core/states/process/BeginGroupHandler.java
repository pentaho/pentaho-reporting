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
