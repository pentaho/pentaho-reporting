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

import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;

/**
 * A report function that counts the total number of items contained in groups in a report. Resets the
 * counter with each new page, and with each new group if the optional group parameter is specified.
 * The function will always reset with each new page, so if a group spans across a page break the
 * counter will be still be reset.
 * <p/>
 * Like all Total-Functions, this function produces precomputed totals. The function's result is precomputed once
 * and will not change later.
 * <p/>
 * The ItemCount can be used to produce a running row-count for a group or report.
 * <p/>
 * To count the number of groups in a report, use the TotalGroupCountFunction.
 *
 * @author Thomas Morgner
 */
public class TotalPageItemCountFunction extends TotalItemCountFunction implements PageEventListener
{
  /**
   * holds the collection of values associated with pages and groups
   */
  private PageGroupValues values = new PageGroupValues();

  private int pageIndex = 0;
  private int groupIndex = 0;

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

  public void groupStarted(final ReportEvent event)
  {
    super.groupStarted(event);
    groupIndex++;
  }

  public void groupFinished(final ReportEvent event)
  {
    storeValue(event);
  }

  /**
   * Handles the pageStartedEvent.
   *
   * @param event the report event.
   */
  public void pageStarted(final ReportEvent event)
  {
    pageIndex++;
  }

  /**
   * Handles the pageFinishedEvent.
   *
   * @param event the report event.
   */
  public void pageFinished(final ReportEvent event)
  {
    storeValue(event);
    clear();
  }

  public Object getValue() {
    return values.get(pageIndex, groupIndex);
  }

  private void storeValue(final ReportEvent event)
  {
    if (isPrepareRunLevel(event))
    {
      values.put(pageIndex, groupIndex, super.getValue());
    }
  }

  /**
   * Convenience class to manage getting and putting values stored
   * by page and group.
   */
  private class PageGroupValues {
    private Map<Integer, Map<Integer, Object>> pagedResults =
        new HashMap<Integer, Map<Integer, Object>>();;

    Object get(int page, int group) {
      if (pagedResults.containsKey(page) &&
          pagedResults.get(page).containsKey(group))
      {
        return pagedResults.get(page).get(group);
      }
      else
      {
        return 0;
      }
    }

    void put(int page, int group, Object value) {
      Map<Integer, Object> map;
      if (pagedResults.containsKey(page))
      {
        map = pagedResults.get(page);
      }
      else
      {
        map = new HashMap<Integer, Object>();
      }
      map.put(group, value);
      pagedResults.put(page, map);
    }

  }
}