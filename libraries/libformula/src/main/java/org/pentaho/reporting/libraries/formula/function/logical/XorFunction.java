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
public class XorFunction implements Function {
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final long serialVersionUID = 1593151240024848931L;


  public XorFunction() {
  }

  public String getCanonicalName() {
    return "XOR";
  }


  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    if ( parameters.getParameterCount() < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    int count = 0;
    final int parameterCount = parameters.getParameterCount();
    for ( int i = 0; i < parameterCount; i++ ) {
      final Type conditionType = parameters.getType( i );
      final Object conditionValue = parameters.getValue( i );
      final Boolean condition = context.getTypeRegistry().convertToLogical( conditionType, conditionValue );
      if ( condition == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      if ( Boolean.TRUE.equals( condition ) ) {
        count += 1;
      }
    }
    if ( count % 2 == 1 ) {
      return RETURN_TRUE;
    } else {
      return RETURN_FALSE;
    }

  }
}
