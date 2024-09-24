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
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function returns the maximum from a set of numbers.
 *
 * @author Cedric Pronzato
 */
public class MaxFunction implements Function {

  private static final long serialVersionUID = -836670420852371100L;

  public MaxFunction() {
  }


  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      return new TypeValuePair( NumberType.GENERIC_NUMBER, BigDecimal.ZERO );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    BigDecimal last = null;
    for ( int paramIdx = 0; paramIdx < parameterCount; paramIdx++ ) {
      final Type type = parameters.getType( paramIdx );
      final Object value = parameters.getValue( paramIdx );
      final Sequence sequence = typeRegistry.convertToNumberSequence( type, value, isStrictSequenceNeeded() );

      while ( sequence.hasNext() ) {
        final LValue rawValue = sequence.nextRawValue();
        if ( rawValue == null ) {
          throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
        }
        final TypeValuePair nextValue = rawValue.evaluate();
        final Number number = typeRegistry.convertToNumber( nextValue.getType(), nextValue.getValue() );
        final BigDecimal next = NumberUtil.getAsBigDecimal( number );

        if ( last == null || last.compareTo( next ) < 0 ) {
          last = next;
        }
      }
    }

    if ( last == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, last );
  }

  protected boolean isStrictSequenceNeeded() {
    return true;
  }

  public String getCanonicalName() {
    return "MAX";
  }
}
