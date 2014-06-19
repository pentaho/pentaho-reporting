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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This function hides all elements with a given name, as long as the defined <code>field</code> does <b>not</b> contain
 * the element name.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class HideElementByNameFunction extends AbstractElementFormatFunction
{
  /**
   * The name of the data-row column that is checked for null-values.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public HideElementByNameFunction()
  {
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField()
  {
    return field;
  }

  /**
   * Defines the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field the field name.
   */
  public void setField(final String field)
  {
    this.field = field;
  }

  protected boolean evaluateElement(final ReportElement e)
  {
    if (ObjectUtilities.equal(e.getName(), getElement()))
    {
      final boolean visible = ObjectUtilities.equal(getElement(), getDataRow().get(getField()));
      e.getStyle().setStyleProperty(ElementStyleKeys.VISIBLE, visible);
      return true;
    }
    return false;
  }
}
