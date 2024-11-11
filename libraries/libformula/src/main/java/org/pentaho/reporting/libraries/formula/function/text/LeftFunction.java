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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * This function returns a selected number of text characters from the left.<br/> This function depends on
 * <code>MidFunction</code>.
 *
 * @author Cedric Pronzato
 * @see MidFunction
 */
public class LeftFunction implements Function {
  private static final long serialVersionUID = 7929942586373275084L;

  public LeftFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );

    final String text = typeRegistry.convertToText( textType, textValue );
    final int length;
    if ( parameterCount == 2 ) {
      final Type lengthType = parameters.getType( 1 );
      final Object lengthValue = parameters.getValue( 1 );
      final Number lengthConv = typeRegistry.convertToNumber( lengthType, lengthValue );
      if ( lengthConv.doubleValue() < 0 ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }

      length = lengthConv.intValue();
    } else {
      length = 1;
    }

    if ( text == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    // Note that MID(T;1;Length) produces the same results as LEFT(T;Length).
    return new TypeValuePair( TextType.TYPE, MidFunction.process( text, 1, length ) );
  }

  public String getCanonicalName() {
    return "LEFT";
  }

}
