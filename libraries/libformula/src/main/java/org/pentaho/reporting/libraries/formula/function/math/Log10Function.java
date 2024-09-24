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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function returns the acos of the value.
 *
 * @author ocke
 * @noinspection UnpredictableBigDecimalConstructorCall
 */
public class Log10Function implements Function {
  private static final BigDecimal LOG10BASE = new BigDecimal( StrictMath.log( 10 ) );

  public String getCanonicalName() {
    return "LOG10";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final Number result = context.getTypeRegistry().convertToNumber( type1, value1 );
    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final double d = StrictMath.log( result.doubleValue() );

    final BigDecimal divide =
      new BigDecimal( d ).divide( LOG10BASE, LibFormulaBoot.GLOBAL_SCALE, BigDecimal.ROUND_HALF_UP );
    return new TypeValuePair( NumberType.GENERIC_NUMBER, NumberUtil.removeTrailingZeros( divide ) );
  }
}
