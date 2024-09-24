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
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;

/**
 * This function returns the acos of the value.
 *
 * @author ocke
 */
public class VarFunction implements Function {

  public String getCanonicalName() {
    return "VAR";
  }

  public TypeValuePair evaluate( FormulaContext context, ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    double fSum = 0.0;
    double vSum = 0.0;
    double vMean = 0.0;
    double nValCount = 0.0;
    double[] values = new double[ parameterCount ];
    for ( int i = 0; i < parameterCount; i++ ) {

      final Type type1 = parameters.getType( i );
      final Object value1 = parameters.getValue( i );
      final Number result = context.getTypeRegistry().convertToNumber( type1, value1 );
      if ( result == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      final double d = result.doubleValue();
      fSum += d;
      values[ i ] = d;
      nValCount++;
    }
    vMean = fSum / nValCount;
    for ( int i = 0; i < nValCount; i++ ) {
      vSum += ( values[ i ] - vMean ) * ( values[ i ] - vMean );
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( vSum / ( nValCount - 1.0 ) ) );
  }
}
