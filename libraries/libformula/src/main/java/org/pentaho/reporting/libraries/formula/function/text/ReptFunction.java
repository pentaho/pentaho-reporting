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
 * This function returns text repeated Count times.
 *
 * @author Cedric Pronzato
 */
public class ReptFunction implements Function {
  private static final long serialVersionUID = -6832781189129832501L;

  public ReptFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type countType = parameters.getType( 1 );
    final Object countValue = parameters.getValue( 1 );

    final int count = typeRegistry.convertToNumber( countType, countValue ).intValue();
    if ( count < 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final Type textType1 = parameters.getType( 0 );
    final Object textValue1 = parameters.getValue( 0 );
    final String rawText = typeRegistry.convertToText( textType1, textValue1 );
    if ( rawText == null ) {
      return new TypeValuePair( TextType.TYPE, "" );
    }

    final StringBuffer buffer = new StringBuffer( rawText.length() * count );
    for ( int i = 0; i < count; i++ ) {
      buffer.append( rawText );
    }
    return new TypeValuePair( TextType.TYPE, buffer.toString() );
  }

  public String getCanonicalName() {
    return "REPT";
  }

}
