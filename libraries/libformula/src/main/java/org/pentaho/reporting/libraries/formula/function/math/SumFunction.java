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
import org.pentaho.reporting.libraries.formula.typing.NumberSequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Creation-Date: 31.10.2006, 17:39:19
 *
 * @author Thomas Morgner
 */
public class SumFunction implements Function {

  private static final long serialVersionUID = -8604838130517819412L;

  public SumFunction() {
  }

  public String getCanonicalName() {
    return "SUM";
  }

  protected boolean isStrictSequenceNeeded() {
    return true;
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    BigDecimal computedResult = BigDecimal.ZERO;
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    for ( int paramIdx = 0; paramIdx < parameterCount; paramIdx++ ) {
      final NumberSequence sequence = convertToNumberSequence( context, parameters, paramIdx );

      while ( sequence.hasNext() ) {
        computedResult = compute( sequence.nextNumber(), computedResult );
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, computedResult );
  }

  protected NumberSequence convertToNumberSequence( final FormulaContext context, final ParameterCallback parameters,
                                                    int paramIdx )
    throws EvaluationException {
    Type type = parameters.getType( paramIdx );
    Object value = parameters.getValue( paramIdx );
    return context.getTypeRegistry().convertToNumberSequence( type, value, isStrictSequenceNeeded() );
  }

  private BigDecimal compute( final Number value,
                              final BigDecimal computedResult ) {
    if ( value == null ) {
      // no-op ..
      return computedResult;
    }

    return computedResult.add( NumberUtil.getAsBigDecimal( value ) );
  }
}
