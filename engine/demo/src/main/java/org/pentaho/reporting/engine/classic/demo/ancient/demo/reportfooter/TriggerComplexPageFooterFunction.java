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
