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

package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Creation-Date: 04.11.2006, 18:28:15
 *
 * @author Thomas Morgner
 */
public class XorFunction implements Function
{
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(LogicalType.TYPE, Boolean.FALSE);
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(LogicalType.TYPE, Boolean.TRUE);
  private static final long serialVersionUID = 1593151240024848931L;


  public XorFunction()
  {
  }

  public String getCanonicalName()
  {
    return "XOR";
  }


  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters)
      throws EvaluationException
  {
    if(parameters.getParameterCount() < 1)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    int count = 0;
    final int parameterCount = parameters.getParameterCount();
    for (int i = 0; i < parameterCount; i++)
    {
      final Type conditionType = parameters.getType(i);
      final Object conditionValue = parameters.getValue(i);
      final Boolean condition = context.getTypeRegistry().convertToLogical(conditionType, conditionValue);
      if(condition == null)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
      }
      if (Boolean.TRUE.equals(condition))
      {
        count += 1;
      }
    }
    if (count % 2 == 1)
    {
      return RETURN_TRUE;
    }
    else
    {
      return RETURN_FALSE;
    }

  }
}
