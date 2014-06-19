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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class PrintSummaryEndCrosstabColumnAxisHandler implements AdvanceHandler
{
  public static final AdvanceHandler HANDLER = new PrintSummaryEndCrosstabColumnAxisHandler();

  public PrintSummaryEndCrosstabColumnAxisHandler()
  {
  }

  public ProcessState advance(final ProcessState state) throws ReportProcessingException
  {
    final ProcessState next = state.deriveForAdvance();
    next.leavePresentationGroup();
    final Group group = next.getReport().getGroup(state.getCurrentGroupIndex());
    if (group instanceof CrosstabColumnGroup)
    {
      next.fireReportEvent();
    }
    return next;
  }

  public ProcessState commit(final ProcessState next) throws ReportProcessingException
  {
    next.setAdvanceHandler(PrintSummaryJoinEndCrosstabColumnAxisHandler.HANDLER);
    return next;
  }

  public boolean isFinish()
  {
    return false;
  }

  public int getEventCode()
  {
    return ReportEvent.SUMMARY_ROW | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler()
  {
    return false;
  }
}
