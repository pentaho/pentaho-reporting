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

/**
 * This function uses an index to return a value from a list of values. The first value index is 1, 2 for the second and
 * so on.
 *
 * @author Cedric Pronzato
 */
public class ChooseFunction implements Function {
  private static final long serialVersionUID = 5328221291584681439L;

  public ChooseFunction() {
  }

  public String getCanonicalName() {
    return "CHOOSE";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {

    if ( parameters.getParameterCount() <= 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Type indexType = parameters.getType( 0 );
    final Object indexValue = parameters.getValue( 0 );

    final int index = context.getTypeRegistry().convertToNumber( indexType, indexValue ).intValue();
    if ( index < 1 || index >= parameters.getParameterCount() ) {
      // else
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    return new TypeValuePair( parameters.getType( index ), parameters.getValue( index ) );
  }
}
