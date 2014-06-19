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

import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;

/**
 * Creation-Date: 03.07.2007, 13:57:49
 *
 * @author Thomas Morgner
 */
public class ProcessDetailsHandler implements AdvanceHandler
{
  public static final AdvanceHandler HANDLER = new ProcessDetailsHandler();

  private ProcessDetailsHandler()
  {
  }

  public int getEventCode()
  {
    return ReportEvent.ITEMS_ADVANCED;
  }

  public ProcessState advance(final ProcessState state) throws ReportProcessingException
  {
    final ProcessState next = state.deriveForAdvance();
    next.fireReportEvent();

    final ItemBand childs = next.getReport().getItemBand();
    if (childs != null)
    {
      return InlineSubreportProcessor.processInline(next, childs);
    }
    return next;
  }


  public ProcessState commit(final ProcessState next) throws ReportProcessingException
  {
    final ItemBand childs = next.getReport().getItemBand();
    if (childs == null)
    {
      return JoinDetailsHandler.HANDLER.commit(next);
    }

    if (InlineSubreportProcessor.hasSubReports(next, childs))
    {
      next.setAdvanceHandler(JoinDetailsHandler.HANDLER);
      return InlineSubreportProcessor.processBandedSubReports(next, childs);
    }

    return JoinDetailsHandler.HANDLER.commit(next);
  }

  public boolean isFinish()
  {
    return false;
  }

  public boolean isRestoreHandler()
  {
    return false;
  }
}
