/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.function.information.InformationFunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

public class ExportTypeFunctionDescription extends AbstractFunctionDescription
{
  /**
   * Default Constructor.
   */
  public ExportTypeFunctionDescription()
  {
    super("EXPORTTYPE", "org.pentaho.reporting.engine.classic.core.function.formula.ExportType-Function");
  }

  /**
   * Returns the expected value type. This function returns a LogicalType.
   *
   * @return LogicalType.TYPE
   */
  public Type getValueType()
  {
    return TextType.TYPE;
  }

  /**
   * Returns the number of parameters expected by the function.
   *
   * @return 1.
   */
  public int getParameterCount()
  {
    return 0;
  }

  /**
   * Returns the parameter type of the function parameters.
   *
   * @param position the parameter index.
   * @return always TextType.TYPE.
   */
  public Type getParameterType(final int position)
  {
    return TextType.TYPE;
  }

  /**
   * Defines, whether the parameter at the given position is mandatory. A mandatory parameter must be filled in, while
   * optional parameters need not to be filled in.
   *
   * @param position the position of the parameter.
   * @return true, as all parameters are mandatory.
   */
  public boolean isParameterMandatory(final int position)
  {
    return false;
  }

  /**
   * Returns the function category. The function category groups functions by their expected use.
   *
   * @return InformationFunctionCategory.CATEGORY.
   */
  public FunctionCategory getCategory()
  {
    return InformationFunctionCategory.CATEGORY;
  }
}
