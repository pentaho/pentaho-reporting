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

import org.pentaho.reporting.libraries.base.util.URLEncoder;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.io.UnsupportedEncodingException;

/**
 * This function encodes a given text using the URL-Encoding schema. An optional second parameter can be given to
 * specify the character encoding that should be used when converting text to bytes.
 *
 * @author Cedric Pronzato
 */
public class URLEncodeFunction implements Function {
  private static final long serialVersionUID = 646428113862238221L;

  public URLEncodeFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );
    final String textResult =
      context.getTypeRegistry().convertToText( textType, textValue );

    if ( textResult == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final String encodingResult;
    if ( parameterCount == 2 ) {
      final Type encodingType = parameters.getType( 1 );
      final Object encodingValue = parameters.getValue( 1 );
      encodingResult = context.getTypeRegistry().convertToText( encodingType, encodingValue );
      if ( encodingResult == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
    } else {
      encodingResult = context.getConfiguration().getConfigProperty
        ( "org.pentaho.reporting.libraries.formula.URLEncoding", "UTF-8" );
    }
    try {
      return new TypeValuePair
        ( TextType.TYPE, URLEncoder.encode( textResult, encodingResult ) );

    } catch ( final UnsupportedEncodingException use ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
  }

  public String getCanonicalName() {
    return "URLENCODE";
  }

}
