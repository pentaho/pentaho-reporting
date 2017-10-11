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
