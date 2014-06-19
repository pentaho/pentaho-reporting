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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * Creation-Date: 31.10.2006, 17:41:12
 *
 * @author Thomas Morgner
 */
public class SumFunctionDescription extends AbstractFunctionDescription
{
  private static final long serialVersionUID = 5844556222254305990L;

  public SumFunctionDescription()
  {
    super("SUM", "org.pentaho.reporting.libraries.formula.function.math.Sum-Function");
  }

  public Type getValueType()
  {
    return NumberType.GENERIC_NUMBER;
  }

  public int getParameterCount()
  {
    return 0;
  }

  public boolean isInfiniteParameterCount()
  {
    return true;
  }

  public Type getParameterType(final int position)
  {
    return NumberType.NUMBER_SEQUENCE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A
   * mandatory parameter must be filled in, while optional parameters need not
   * to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory(final int position)
  {
    return false;
  }

  public FunctionCategory getCategory()
  {
    return MathFunctionCategory.CATEGORY;
  }
}
