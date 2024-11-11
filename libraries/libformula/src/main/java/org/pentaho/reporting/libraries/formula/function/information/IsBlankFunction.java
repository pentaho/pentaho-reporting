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
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Document me!
 *
 * @author : Thomas Morgner
 */
public class IsBlankFunction implements Function {
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(
    LogicalType.TYPE, Boolean.FALSE );

  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(
    LogicalType.TYPE, Boolean.TRUE );
  private static final long serialVersionUID = 3352413434463488403L;

  public IsBlankFunction() {
  }

  public String getCanonicalName() {
    return "ISBLANK";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    try {
      final Object value = parameters.getValue( 0 );
      final LValue raw = parameters.getRaw( 0 );
      if ( raw instanceof ContextLookup ) {
        if ( value == null ) {
          return RETURN_TRUE;
        }
      }
    } catch ( EvaluationException e ) {
      if ( e.getErrorValue().getErrorCode() == LibFormulaErrorValue.ERROR_NA ) {
        return RETURN_TRUE;
      }
      return RETURN_FALSE;
    }

    return RETURN_FALSE;
  }
}
