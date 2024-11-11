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
