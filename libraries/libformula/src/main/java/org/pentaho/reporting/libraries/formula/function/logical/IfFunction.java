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

package org.pentaho.reporting.libraries.formula.function.logical;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Creation-Date: 04.11.2006, 18:28:15
 *
 * @author Thomas Morgner
 */
public class IfFunction implements Function {
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = -7517668261071087411L;

  public IfFunction() {
  }

  public String getCanonicalName() {
    return "IF";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Type conditionType = parameters.getType( 0 );
    final Object conditionValue = parameters.getValue( 0 );
    final Boolean condition = context.getTypeRegistry().convertToLogical( conditionType, conditionValue );
    if ( condition == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    if ( Boolean.TRUE.equals( condition ) ) {
      final Object value = parameters.getValue( 1 );
      final Type type = parameters.getType( 1 );
      return new TypeValuePair( type, value );
    }
    // if condition is false and no third parameter, return false
    if ( parameterCount == 2 || parameters.getValue( 2 ) == null ) {
      return RETURN_FALSE;
    }
    // else return third parameter
    final Object value = parameters.getValue( 2 );
    final Type type = parameters.getType( 2 );
    return new TypeValuePair( type, value );
  }
}
