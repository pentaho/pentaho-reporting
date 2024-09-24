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
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * This function returns the given text free of leading spaces. Removes all leading and trailing spaces and all extra
 * spaces inside the text.
 *
 * @author Cedric Pronzato
 * @see http://mercury.ccil.org/%7Ecowan/OF/textfuncs.html
 */
public class TrimFunction implements Function {
  private static final long serialVersionUID = 7379670108270974597L;

  public TrimFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final String result = context.getTypeRegistry().convertToText( type1, value1 );

    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    // remove all unnecessary spaces ..
    // we dont use regexps, because they are JDK 1.4, but this library is aimed
    // for JDK 1.2.2

    final char[] chars = result.toCharArray();
    final StringBuffer b = new StringBuffer( chars.length );
    boolean removeNextWs = true;

    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      if ( Character.isWhitespace( c ) ) {
        if ( removeNextWs ) {
          continue;
        }
        b.append( c );
        removeNextWs = true;
        continue;
      }

      b.append( c );
      removeNextWs = false;
    }

    // now check whether the last char is a whitespace and remove that one
    // if neccessary
    final String trimmedResult;
    if ( removeNextWs && b.length() > 0 ) {
      trimmedResult = b.substring( 0, b.length() - 1 );
    } else {
      trimmedResult = b.toString();
    }

    return new TypeValuePair( TextType.TYPE, trimmedResult );
  }

  public String getCanonicalName() {
    return "TRIM";
  }

}
