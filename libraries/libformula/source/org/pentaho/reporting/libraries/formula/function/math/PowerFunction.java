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

package org.pentaho.reporting.libraries.formula.function.math;

import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * This function returns the value of one number raised to the power of another number.
 *
 * @author Cedric Pronzato
 */
public class PowerFunction implements Function
{

  public PowerFunction()
  {
  }

  public TypeValuePair evaluate(final FormulaContext context, final ParameterCallback parameters) throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount != 2)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }
    final Type type1 = parameters.getType(0);
    final Object value1 = parameters.getValue(0);
    final Type type2 = parameters.getType(1);
    final Object value2 = parameters.getValue(1);

    final Number result1 = context.getTypeRegistry().convertToNumber(type1, value1);
    final Number result2 = context.getTypeRegistry().convertToNumber(type2, value2);
    if (result1 == null || result2 == null)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }


    final double power = Math.pow(result1.doubleValue(), result2.doubleValue());
    return new TypeValuePair(NumberType.GENERIC_NUMBER,  new BigDecimal(power));
  }

  public String getCanonicalName()
  {
    return "POWER";
  }

}
