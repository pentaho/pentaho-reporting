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

public class LookupFunction implements Function {
  public LookupFunction() {
  }

  public String getCanonicalName() {
    return "LOOKUP";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final String text = context.getTypeRegistry().convertToText( type1, value1 );
    final Type t = context.resolveReferenceType( text );
    final Object o = context.resolveReference( text );
    if ( o == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }
    return new TypeValuePair( t, o );
  }
}
