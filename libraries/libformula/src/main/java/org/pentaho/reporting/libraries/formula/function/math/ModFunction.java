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
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function returns the remainder when one number is divided by another number.
 *
 * @author Cedric Pronzato
 */
public class ModFunction implements Function {
  private static final long serialVersionUID = -2492279311353854670L;

  public ModFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final Number number1 = typeRegistry.convertToNumber( type1, value1 );
    if ( number1 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final BigDecimal divided = NumberUtil.getAsBigDecimal( number1 );

    final Type type2 = parameters.getType( 1 );
    final Object value2 = parameters.getValue( 1 );
    final Number number2 = typeRegistry.convertToNumber( type2, value2 );
    if ( number2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final BigDecimal divisor = NumberUtil.getAsBigDecimal( number2 );
    if ( divisor.signum() == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE );
    }

    final BigDecimal divide = new BigDecimal( divided.divide( divisor, 0, BigDecimal.ROUND_FLOOR ).toString() );
    BigDecimal reminder = divided.subtract( divisor.multiply( divide ) );
    if ( divide.signum() == 0 ) {
      if ( ( divided.signum() == -1 && divisor.signum() != -1 ) || ( divisor.signum() == -1
        && divided.signum() != -1 ) ) {
        reminder = divided.add( divisor );
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, reminder );
  }

  public String getCanonicalName() {
    return "MOD";
  }

}
