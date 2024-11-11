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
 * This function returns the rounding of a number up to the nearest even integer.
 *
 * @author Cedric Pronzato
 */
public class EvenFunction implements Function {
  private static final long serialVersionUID = 2587673708222713810L;

  public EvenFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final Number result = context.getTypeRegistry().convertToNumber( type1, value1 );

    final BigDecimal ret = compute( result );

    return new TypeValuePair( NumberType.GENERIC_NUMBER, ret );
  }

  private static BigDecimal compute( final Number result ) {
    final int intValue;
    if ( result.doubleValue() < 0 ) {
      intValue = (int) Math.floor( result.doubleValue() );
    } else {
      intValue = (int) Math.ceil( result.doubleValue() );
    }

    final BigDecimal ret;
    if ( intValue % 2 == 0 ) // even
    {
      if ( intValue == 0 ) {
        if ( result.doubleValue() < 0 ) {
          ret = new BigDecimal( -2 );
        } else if ( result.doubleValue() > 0 ) {
          ret = new BigDecimal( 2 );
        } else {
          ret = new BigDecimal( 0 );
        }
      } else {
        ret = new BigDecimal( intValue );
      }
    } else
    // odd
    {
      if ( result.doubleValue() < 0 ) {
        ret = new BigDecimal( intValue - 1 );
      } else {
        ret = new BigDecimal( intValue + 1 );
      }
    }
    return ret;
  }

  public String getCanonicalName() {
    return "EVEN";
  }


}
