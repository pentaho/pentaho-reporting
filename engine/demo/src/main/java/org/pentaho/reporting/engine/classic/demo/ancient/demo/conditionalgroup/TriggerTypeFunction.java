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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.conditionalgroup;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;

public class TriggerTypeFunction extends AbstractFunction
{
  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public TriggerTypeFunction()
  {
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    // do nothing if this is the wrong group ...
    if (FunctionUtilities.isDefinedGroup("type-group", event) == false)
    {
      return;
    }

    final String type = (String) event.getDataRow().get("type");
    final RelationalGroup g = (RelationalGroup) FunctionUtilities.getCurrentGroup(event);

    final Element headerIncomeBand = g.getHeader().getElement("income-band");
    final Element headerExpenseBand = g.getHeader().getElement("expense-band");
    final Element footerIncomeBand = g.getFooter().getElement("income-band");
    final Element footerExpenseBand = g.getFooter().getElement("expense-band");

    // and now apply the visibility ...
    if ("Income".equals(type))
    {
      headerExpenseBand.setVisible(false);
      footerExpenseBand.setVisible(false);
      headerIncomeBand.setVisible(true);
      footerIncomeBand.setVisible(true);
    }
    else
    {
      headerExpenseBand.setVisible(true);
      footerExpenseBand.setVisible(true);
      headerIncomeBand.setVisible(false);
      footerIncomeBand.setVisible(false);
    }
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
