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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function retruns true if the given value is an even number.
 *
 * @author Cedric Pronzato
 */
public class IsEvenFunction implements Function {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(
    LogicalType.TYPE, Boolean.TRUE );

  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(
    LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 3298591139016352997L;

  public IsEvenFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Type type1 = parameters.getType( 0 );
    final Object value = parameters.getValue( 0 );

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Number number = typeRegistry.convertToNumber( type1, value );

    if ( number == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    int intValue = number.intValue();
    if ( intValue < 0 ) {
      intValue *= -1;
    }

    if ( intValue % 2 == 0 ) {
      return RETURN_TRUE;
    }

    return RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "ISEVEN";
  }

}
