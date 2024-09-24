/*
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
 * Copyright (c) 2006 - 2018 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.pentaho.reporting.libraries.base.util.StringUtils.isEmpty;

/**
 * This function build url based on parts provided in parameters
 *
 * @author Dmitriy Stepanov
 */
public class URLBuilderFunction implements Function {
  private static final long serialVersionUID = -3929896303552652226L;

  public URLBuilderFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 || parameterCount > 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );
    final String textResult =
      context.getTypeRegistry().convertToText( textType, textValue );

    if ( textResult == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }


    final Type pathType = parameters.getType( 1 );
    final Object pathValue = parameters.getValue( 1 );
    final String pathResult = context.getTypeRegistry().convertToText( pathType, pathValue );
    if ( pathResult == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    String paramResult = null;
    if ( parameterCount == 3 ) {
      final Type paramType = parameters.getType( 1 );
      final Object paramValue = parameters.getValue( 1 );
      paramResult = context.getTypeRegistry().convertToText( paramType, paramValue );
      if ( paramResult == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
    }
    try {
      URI uri = new URI( textResult ).resolve( pathResult ).normalize();
      String query = null;
      if ( !isEmpty( paramResult ) ) {
        if ( uri.getQuery() == null ) {
          query = paramResult;
        } else {
          query = query.concat( "&" ).concat( paramResult );
        }
      } else {
        query = uri.getQuery();
      }
      uri = new URI( uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), query,
        uri.getFragment() );

      return new TypeValuePair( TextType.TYPE, uri.toURL().toString() );

    } catch ( URISyntaxException | MalformedURLException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
  }

  public String getCanonicalName() {
    return "URLBUILDER";
  }

}
