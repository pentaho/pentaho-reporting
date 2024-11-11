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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function returns true if the parameter is of error.
 *
 * @author Cedric Pronzato
 */
public class IsErrorFunction implements Function {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 5692502042198617868L;

  public IsErrorFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    try {
      final Type type = parameters.getType( 0 );
      final Object value = parameters.getValue( 0 );

      if ( ErrorType.TYPE.equals( type ) && value instanceof ErrorValue ) {
        return RETURN_TRUE;
      }
    } catch ( EvaluationException e ) {
      return RETURN_TRUE;
    }

    return RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "ISERROR";
  }

}
