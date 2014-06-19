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
* Copyright (c) 2008 - 2009 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

/**
 * Describes the CountBlankFunction function.
 *
 * @author Cedric Pronzato
 * @see org.pentaho.reporting.libraries.formula.function.information.CountBlankFunction
 */
public class CountBlankFunctionDescription extends AbstractFunctionDescription
{
  public CountBlankFunctionDescription()
  {
    super("COUNTBLANK", "org.pentaho.reporting.libraries.formula.function.information.CountBlank-Function");
  }

  public Type getValueType()
  {
    return NumberType.GENERIC_NUMBER;
  }

  public FunctionCategory getCategory()
  {
    return InformationFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 1;
  }

  /**
   * Returns the parameter type at the given position using the function metadata. The first parameter is at the
   * position 0;
   *
   * @param position The parameter index.
   * @return The parameter type.
   */
  public Type getParameterType(final int position)
  {
    return AnyType.ANY_SEQUENCE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @return
   */
  public boolean isParameterMandatory(final int position)
  {
    return true;
  }
}
