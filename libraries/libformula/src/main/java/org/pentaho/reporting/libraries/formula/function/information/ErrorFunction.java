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

import org.pentaho.reporting.libraries.formula.CustomErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;

public class ErrorFunction implements Function {
  public ErrorFunction() {
  }

  public String getCanonicalName() {
    return "ERROR";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    if ( parameters.getParameterCount() > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Type textType = parameters.getType( 0 );
    final Object textValueRaw = parameters.getValue( 0 );
    final String text = context.getTypeRegistry().convertToText( textType, textValueRaw );

    Number code = null;
    if ( parameters.getParameterCount() == 2 ) {
      final Type codeType = parameters.getType( 1 );
      final Object codeRaw = parameters.getValue( 1 );
      code = context.getTypeRegistry().convertToNumber( codeType, codeRaw );
    }
    if ( code == null ) {
      code = -1;
    }
    throw EvaluationException.getInstance( new CustomErrorValue( code.intValue(), text ) );
  }
}
