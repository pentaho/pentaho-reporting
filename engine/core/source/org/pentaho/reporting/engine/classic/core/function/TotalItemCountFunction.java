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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

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
public class TotalItemCountFunction extends AbstractFunction implements AggregationFunction
{
  /**
   * A map of results, keyed by the process-key.
   */
  private transient HashMap<ReportStateKey, Sequence<Integer>> results;
  private transient int lastGroupSequenceNumber;

  /**
   * The name of the group on which to reset.
   */
  private String group;

  /**
   * The current row-count.
   */
  private transient Sequence<Integer> result;
  /**
   * The global state key is used to store the result for the whole report.
   */
  private transient ReportStateKey globalStateKey;

  /**
   * The current group key is used to store the result for the current group.
   */
  private transient ReportStateKey currentGroupKey;


  private String crosstabFilterGroup;

  /**
   * Default constructor.
   */
  public TotalItemCountFunction()
  {
    results = new HashMap<ReportStateKey, Sequence<Integer>>();
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    globalStateKey = event.getState().getProcessKey();
    if (isPrepareRunLevel(event))
    {
      result = new Sequence<Integer>();
      results.clear();
      results.put(globalStateKey, result);
      lastGroupSequenceNumber = 0;
    }
    else
    {
      result = results.get(globalStateKey);
      lastGroupSequenceNumber = 0;
    }
  }

  protected boolean isPrepareRunLevel(final ReportEvent event)
  {
    return FunctionUtilities.isDefinedPrepareRunLevel(this, event);
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    if (FunctionUtilities.isDefinedGroup(getGroup(), event))
    {
      currentGroupKey = event.getState().getProcessKey();
      if (isPrepareRunLevel(event))
      {
        clear();

        results.put(globalStateKey, result);
        results.put(currentGroupKey, result);
      }
      else
      {
        // Activate the current group, which was filled in the prepare run.
        result = results.get(currentGroupKey);
      }
    }

    if (FunctionUtilities.isDefinedGroup(getCrosstabFilterGroup(), event))
    {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter(groupIndex);
    }
  }

  protected void clear()
  {
    result = new Sequence<Integer>();
    lastGroupSequenceNumber = 0;
  }


  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    if (isPrepareRunLevel(event) == false)
    {
      return;
    }

    final Integer oldValue = result.get(lastGroupSequenceNumber);
    if (oldValue == null)
    {
      result.set(lastGroupSequenceNumber, 1);
    }
    else
    {
      result.set(lastGroupSequenceNumber, oldValue + 1);
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    final TotalItemCountFunction o = (TotalItemCountFunction) super.clone();
    o.results =  (HashMap<ReportStateKey, Sequence<Integer>>)results.clone();  

    // Clone saved group results.
    // The currently active result needs to be handled
    // separately from this loop, since the globalStateKey
    // and currentGroupKey both need to be mapped to it.
    for (Map.Entry<ReportStateKey, Sequence<Integer>> entry : results.entrySet()) {
      if (entry.getKey() != globalStateKey && entry.getKey() != currentGroupKey)
      {
         o.results.put(entry.getKey(), entry.getValue().clone());
      }
    }

    if (result != null) {
      o.result = result.clone();
      o.results.put(globalStateKey, o.result);
      o.results.put(currentGroupKey, o.result);
    }
    return o;
  }



  public void summaryRowSelection(final ReportEvent event)
  {
    if (FunctionUtilities.isDefinedGroup(getCrosstabFilterGroup(), event))
    {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter(groupIndex);
    }
  }

  /**
   * Returns the name of the group to be totalled.
   *
   * @return the group name.
   */
  public String getGroup()
  {
    return group;
  }

  /**
   * Defines the name of the group to be totalled. If the name is null, all groups are totalled.
   *
   * @param group the group name.
   */
  public void setGroup(final String group)
  {
    this.group = group;
  }

  /**
   * Returns the number of items counted (so far) by the function.  This is either the number of items in the report, or
   * the group (if a group has been defined for the function).
   *
   * @return The item count.
   */
  public Object getValue()
  {
    if (result == null)
    {
      return 0;
    }

    final Integer value = result.get(lastGroupSequenceNumber);
    if (value == null)
    {
      return 0;
    }
    return value;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    final TotalItemCountFunction function = (TotalItemCountFunction) super.getInstance();
    function.results = new HashMap<ReportStateKey, Sequence<Integer>>();
    return function;
  }

  /**
   * Helper function for the serialization.
   *
   * @param in the input stream.
   * @throws IOException            if an IO error occured.
   * @throws ClassNotFoundException if a required class could not be found.
   */
  private void readObject(final ObjectInputStream in)
      throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    results = new HashMap<ReportStateKey, Sequence<Integer>>();
  }

  public String getCrosstabFilterGroup()
  {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup(final String crosstabFilterGroup)
  {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

}
