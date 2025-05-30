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

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function checks whatever the given value is of Logical type.
 *
 * @author Cedric Pronzato
 */
public class IsLogicalFunction implements Function {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair(
    LogicalType.TYPE, Boolean.TRUE );

  private static final TypeValuePair RETURN_FALSE = new TypeValuePair(
    LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 7918371444174838208L;

  public IsLogicalFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    if ( value1 instanceof Boolean || type1.isFlagSet( Type.LOGICAL_TYPE ) ) {
      return RETURN_TRUE;
    }

    return RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "ISLOGICAL";
  }

}
