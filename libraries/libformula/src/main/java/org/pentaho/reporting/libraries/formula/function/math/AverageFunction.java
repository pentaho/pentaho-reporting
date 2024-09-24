/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function returns the average of the number sequence.
 *
 * @author Cedric Pronzato
 */
public class AverageFunction implements Function {
  private static final long serialVersionUID = -5057715506050635450L;
  private SumFunction sumFunction;

  public AverageFunction() {
    this( new SumFunction() );
  }

  protected AverageFunction( final SumFunction sumFunction ) {
    this.sumFunction = sumFunction;
  }

  public String getCanonicalName() {
    return "AVERAGE";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final TypeValuePair sum = sumFunction.evaluate( context, parameters );

    final Number n = context.getTypeRegistry().convertToNumber( sum.getType(), sum.getValue() );
    if ( n == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final BigDecimal divident = NumberUtil.getAsBigDecimal( n );
    final BigDecimal divisor = new BigDecimal( parameters.getParameterCount() );
    final BigDecimal avg = NumberUtil.divide( divident, divisor );
    return new TypeValuePair( NumberType.GENERIC_NUMBER, avg );
  }
}
