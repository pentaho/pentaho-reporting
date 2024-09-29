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
 * This function returns a selected number of text characters from the right.<br/> This function depends on
 * <code>MidFunction</code>.
 *
 * @author Cedric Pronzato
 * @see MidFunction
 */
public class RightFunction implements Function {
  private static final long serialVersionUID = 1637903638146059530L;

  public RightFunction() {
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
      final Number lengthVal = typeRegistry.convertToNumber( parameters.getType( 1 ), parameters.getValue( 1 ) );
      if ( lengthVal.doubleValue() < 0 ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      length = lengthVal.intValue();
    } else {
      length = 1;
    }

    if ( text == null || length < 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    int s = text.length() - length + 1;
    if ( s < 1 ) {
      s = 1;
    }
    return new TypeValuePair( TextType.TYPE, MidFunction.process( text, s, length ) );
  }

  public String getCanonicalName() {
    return "RIGHT";
  }

}
