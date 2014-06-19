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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function checks whatever the given value is of Logical type.
 * 
 * @author Cedric Pronzato
 * 
 */
public class IsLogicalFunction implements Function
{
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(
      LogicalType.TYPE, Boolean.TRUE);

  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(
      LogicalType.TYPE, Boolean.FALSE);
  private static final long serialVersionUID = 7918371444174838208L;

  public IsLogicalFunction()
  {
  }

  public TypeValuePair evaluate(final FormulaContext context,
      final ParameterCallback parameters) throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount < 1)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

     final Type type1 = parameters.getType(0);
     final Object value1 = parameters.getValue(0);
     if(value1 instanceof Boolean || type1.isFlagSet(Type.LOGICAL_TYPE))
     {
       return RETURN_TRUE;
     }

    return RETURN_FALSE;
  }

  public String getCanonicalName()
  {
    return "ISLOGICAL";
  }

}
