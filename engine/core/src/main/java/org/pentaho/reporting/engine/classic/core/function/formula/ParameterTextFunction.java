/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
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

public class ParameterTextFunction implements Function {
  private static final Log logger = LogFactory.getLog( ParameterTextFunction.class );

  public ParameterTextFunction() {
  }

  public String getCanonicalName() {
    return "PARAMETERTEXT";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Object rawValue = parameters.getValue( 0 );
    if ( rawValue == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final String s;
    try {
      s = ConverterRegistry.toAttributeValue( rawValue );
    } catch ( BeanException e ) {
      // ok, so what. Log and return error
      logger.warn( "PARAMETERTEXT: Failed to convert value " + rawValue + " (" + rawValue.getClass() + ")", e );
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }

    if ( s == null ) {
      return new TypeValuePair( TextType.TYPE, "" );
    }
    final String encodingResult;
    if ( parameterCount > 1 ) {
      final Object urlEncode =
          context.getTypeRegistry().convertToLogical( parameters.getType( 1 ), parameters.getValue( 1 ) );
      if ( Boolean.FALSE.equals( urlEncode ) ) {
        return new TypeValuePair( TextType.TYPE, s );
      }

      if ( parameterCount == 3 ) {
        final Type encodingType = parameters.getType( 2 );
        final Object encodingValue = parameters.getValue( 2 );
        encodingResult = context.getTypeRegistry().convertToText( encodingType, encodingValue );
        if ( encodingResult == null ) {
          throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
        }
      } else {
        encodingResult =
            context.getConfiguration().getConfigProperty( "org.pentaho.reporting.libraries.formula.URLEncoding",
                "UTF-8" );
      }
    } else {
      encodingResult =
          context.getConfiguration().getConfigProperty( "org.pentaho.reporting.libraries.formula.URLEncoding", "UTF-8" );
    }

    try {
      return new TypeValuePair( TextType.TYPE, URLEncoder.encode( s, encodingResult ) );

    } catch ( final UnsupportedEncodingException use ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
  }
}
