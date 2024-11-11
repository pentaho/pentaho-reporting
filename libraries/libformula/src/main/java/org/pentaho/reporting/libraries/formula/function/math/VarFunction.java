/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
