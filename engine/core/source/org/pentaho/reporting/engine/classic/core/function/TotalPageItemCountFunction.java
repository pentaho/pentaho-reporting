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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;

/**
 * A report function that counts the total number of items contained in groups in a report. If no groupname is given,
 * all items of the report are counted.
 * <p/>
 * Like all Total-Functions, this function produces a precomputed grand total. The function's result is precomputed once
 * and will not change later. Printing the result of this function in a group header returns the same value as printed
 * in the group-footer.
 * <p/>
 * The ItemCount can be used to produce a running row-count for a group or report.
 * <p/>
 * To count the number of groups in a report, use the TotalGroupCountFunction.
 *
 * @author Thomas Morgner
 */
public class TotalPageItemCountFunction extends TotalItemCountFunction implements PageEventListener
{
  public TotalPageItemCountFunction()
  {
  }

  protected boolean isPrepareRunLevel(final ReportEvent event)
  {
    if (event.getState().isPrepareRun() && event.getState().getLevel() == LayoutProcess.LEVEL_PAGINATE)
    {
      return true;
    }
    return false;
  }

  /**
   * Handles the pageStartedEvent.
   *
   * @param event the report event.
   */
  public void pageStarted(final ReportEvent event)
  {
  }

  /**
   * Handles the pageFinishedEvent.
   *
   * @param event the report event.
   */
  public void pageFinished(final ReportEvent event)
  {
    if (isPrepareRunLevel(event))
    {
      clear();
    }
  }
}