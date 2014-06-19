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

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.function.AbstractFunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionCategory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

public class ArrayLeftFunctionDescription extends AbstractFunctionDescription
{
  public ArrayLeftFunctionDescription()
  {
    super("ARRAYLEFT", "org.pentaho.reporting.libraries.formula.function.userdefined.ArrayLeft-Function");
  }

  public Type getValueType()
  {
    return AnyType.ANY_ARRAY;
  }

  public FunctionCategory getCategory()
  {
    return UserDefinedFunctionCategory.CATEGORY;
  }

  public int getParameterCount()
  {
    return 2;
  }

  public Type getParameterType(final int position)
  {
    if (position == 0)
    {
      return AnyType.ANY_ARRAY;
    }

    return NumberType.GENERIC_NUMBER;
  }

  public boolean isParameterMandatory(final int position)
  {
    if (position == 2)
    {
      return false;
    }
    return true;
  }

}
