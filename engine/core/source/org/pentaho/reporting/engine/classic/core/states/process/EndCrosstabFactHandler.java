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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

public class EndCrosstabFactHandler implements AdvanceHandler
{
  public static final AdvanceHandler HANDLER = new EndCrosstabFactHandler();

  private EndCrosstabFactHandler()
  {
  }

  public ProcessState advance(final ProcessState state) throws ReportProcessingException
  {
    final ProcessState next = state.deriveForAdvance();
    next.setInItemGroup(false);
    next.fireReportEvent();
    return next;
  }

  public ProcessState commit(final ProcessState state) throws ReportProcessingException
  {
    state.setAdvanceHandler(EndCrosstabColumnBodyHandler.HANDLER);
    return state;
  }

  public boolean isFinish()
  {
    return false;
  }

  public int getEventCode()
  {
    return ReportEvent.ITEMS_FINISHED | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler()
  {
    return false;
  }
}
