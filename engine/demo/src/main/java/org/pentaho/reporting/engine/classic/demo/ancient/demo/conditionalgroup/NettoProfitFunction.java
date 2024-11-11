/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.ancient.demo.conditionalgroup;

import java.math.BigDecimal;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

public class NettoProfitFunction extends AbstractFunction
{
  private BigDecimal income;
  private BigDecimal expense;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public NettoProfitFunction()
  {
  }

  /**
   * Receives notification that report generation initializes the current run. <P> The event carries a
   * ReportState.Started state.  Use this to initialize the report.
   *
   * @param event The event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    income = null;
    expense = null;
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    // do nothing if this is the wrong group ...
    if (FunctionUtilities.isDefinedGroup("type-group", event) == false)
    {
      return;
    }
    final String type = (String) event.getDataRow().get("type");

    if ("Income".equals(type))
    {
      final Number n = (Number) event.getDataRow().get("sum");
      income = new BigDecimal(n.toString());
    }
    else
    {
      final Number n = (Number) event.getDataRow().get("sum");
      expense = new BigDecimal(n.toString());
    }
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    if (income != null && expense != null)
    {
      return income.subtract(expense);
    }
    else
    {
      return null;
    }
  }
}
