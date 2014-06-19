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

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Describes ReplaceFunction function.
 * @see ReplaceFunction
 *
 * @author Cedric Pronzato
 *
 */
public class ReplaceFunctionDescription extends AbstractFunctionDescription
{
  private static final long serialVersionUID = -7429162918022452367L;

  public ReplaceFunctionDescription()
  {
    super("REPLACE", "org.pentaho.reporting.libraries.formula.function.text.Replace-Function");
  }

  public FunctionCategory getCategory()
  {
    return TextFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 4;
  }

  public Type getParameterType(final int position)
  {
    if(position == 0 || position == 3)
    {
      return TextType.TYPE;
    }
    return NumberType.GENERIC_NUMBER;
  }

  public Type getValueType()
  {
    return TextType.TYPE;
  }

  public boolean isParameterMandatory(final int position)
  {
    return true;
  }

}
