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

/**
 * Checks whether a field is empty. A field is considered empty, if it contains the value 'null', or an string that is
 * empty or only consists of whitespaces or a number that evaluates to zero.
 *
 * @author Thomas Morgner
 * @deprecated Use a Formula Instead
 */
public class IsEmptyExpression extends AbstractExpression
{
  /**
   * The field name.
   */
  private String field;

  /**
   * Default constructor.
   */
  public IsEmptyExpression()
  {
  }

  /**
   * Returns the name of the field from where to read the value.
   *
   * @return the field.
   */
  public String getField()
  {
    return field;
  }

  /**
   * Defines the name of the field from where to read the value.
   *
   * @param field the field.
   */
  public void setField(final String field)
  {
    this.field = field;
  }

  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    final Object o = getDataRow().get(getField());
    if (o == null)
    {
      return Boolean.TRUE;
    }
    if (o instanceof String)
    {
      final String s = (String) o;
      if (s.trim().length() == 0)
      {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    if (o instanceof Number)
    {
      final Number n = (Number) o;
      if (n.doubleValue() == 0)
      {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    return Boolean.FALSE;
  }
}
