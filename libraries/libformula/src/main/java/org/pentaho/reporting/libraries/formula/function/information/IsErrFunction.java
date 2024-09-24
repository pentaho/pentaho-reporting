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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * This function returns true if the parameter is of error and not of error type NA.
 *
 * @author Cedric Pronzato
 */
public class IsErrFunction implements Function {
  private static final Log logger = LogFactory.getLog( IsErrFunction.class );
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 6749192734608313367L;

  public IsErrFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    try {
      final Type type = parameters.getType( 0 );
      final Object value = parameters.getValue( 0 );

      if ( ErrorType.TYPE.equals( type ) && value instanceof ErrorValue ) {
        logger.warn( "Passing errors around is deprecated. Throw exceptions instead." );
        final ErrorValue na = (ErrorValue) value;
        if ( na.getErrorCode() == LibFormulaErrorValue.ERROR_NA ) {
          return RETURN_FALSE;
        } else {
          return RETURN_TRUE;
        }
      }
    } catch ( EvaluationException e ) {
      if ( e.getErrorValue().getErrorCode() == LibFormulaErrorValue.ERROR_NA ) {
        return RETURN_FALSE;
      } else {
        return RETURN_TRUE;
      }
    }

    return RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "ISERR";
  }

}
