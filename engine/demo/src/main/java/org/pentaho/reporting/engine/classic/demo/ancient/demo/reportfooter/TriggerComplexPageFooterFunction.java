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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.reportfooter;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;

public class TriggerComplexPageFooterFunction extends AbstractFunction
{
  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public TriggerComplexPageFooterFunction()
  {
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event The event.
   */
  public void reportDone(final ReportEvent event)
  {
    event.getReport().getPageFooter().getElement("page-footer-content").setVisible(false);
    event.getReport().getPageFooter().getElement("fake-report-footer").setVisible(true);
  }

  /**
   * Receives notification that report generation initializes the current run. <P> The event carries a
   * ReportState.Started state.  Use this to initialize the report.
   *
   * @param event The event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    event.getReport().getPageFooter().getElement("page-footer-content").setVisible(true);
    event.getReport().getPageFooter().getElement("fake-report-footer").setVisible(false);
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    return null;
  }
}
