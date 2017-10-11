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

public class TriggerNestedGroupFunction extends AbstractFunction
{
  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public TriggerNestedGroupFunction()
  {
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event the event.
   */
  public void groupStarted(final ReportEvent event)
  {
    // first check, if this event activates the correct group
    if (FunctionUtilities.isDefinedGroup("conditional-level-group", event) == false)
    {
      return;
    }
    // and if so, then get a reference to that group
    final RelationalGroup group = (RelationalGroup) FunctionUtilities.getCurrentGroup(event);

    // check, if all required bands are defined and return if one
    // of them is missing.
    final Element normalItemBand =
        event.getReport().getItemBand().getElement("FirstLevel");
    final Element nestedItemBand =
        event.getReport().getItemBand().getElement("SecondLevel");
    if (normalItemBand == null || nestedItemBand == null)
    {
      return;
    }

    // and now apply the visiblity to all bands affected
    final boolean isNestedGroup =
        (event.getDataRow().get("level-two-account") != null);
    //Log.warn("Is Nested Group: " + event.getDataRow().get("level-one-account") + " -> " + isNestedGroup);
    if (isNestedGroup)
    {
      normalItemBand.setVisible(false);
      nestedItemBand.setVisible(true);
      group.getHeader().setVisible(true);
      group.getFooter().setVisible(true);
    }
    else
    {
      normalItemBand.setVisible(true);
      nestedItemBand.setVisible(false);
      group.getHeader().setVisible(false);
      group.getFooter().setVisible(false);
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
