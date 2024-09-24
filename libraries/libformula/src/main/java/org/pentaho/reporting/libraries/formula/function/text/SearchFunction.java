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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This function returns the starting position of a given text in the given text.
 *
 * @author Cedric Pronzato
 */
public class SearchFunction implements Function {
  private static final long serialVersionUID = -6581390286475368968L;

  public SearchFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 || parameterCount > 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type searchType = parameters.getType( 0 );
    final Object searchValue = parameters.getValue( 0 );
    final Type textType = parameters.getType( 1 );
    final Object textValue = parameters.getValue( 1 );
    Type indexType = null;
    Object indexValue = null;

    if ( parameterCount == 3 ) {
      indexType = parameters.getType( 2 );
      indexValue = parameters.getValue( 2 );

      if ( indexType == null || indexValue == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_MISSING_ARGUMENT_VALUE );
      }
    }

    final String search = typeRegistry.convertToText( searchType, searchValue );
    final String text = typeRegistry.convertToText( textType, textValue );

    if ( search == null || text == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    int indexFrom = 0;

    if ( indexType != null ) {
      final Number n = typeRegistry.convertToNumber( indexType, indexValue );
      if ( n.intValue() >= 1 ) {
        indexFrom = n.intValue() - 1;
      } else {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
    }

    final StringBuffer b = new StringBuffer();
    final char[] chars = search.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char aChar = chars[ i ];
      if ( aChar == '?' ) {
        b.append( ".{1}+" );
      } else if ( aChar == '*' ) {
        b.append( ".*?" );
      } else if ( aChar == '.' ||
        aChar == '{' ||
        aChar == '}' ||
        aChar == '(' ||
        aChar == ')' ||
        aChar == '+' ||
        aChar == '*' ||
        aChar == '\\' ||
        aChar == '?' ) {
        // blindly quote all characters. It should not cause any harm ..
        b.append( '\\' );
        b.append( aChar );
      } else {
        b.append( aChar );
      }
    }
    final Pattern patter = Pattern.compile( b.toString(), Pattern.CASE_INSENSITIVE );
    final Matcher matcher = patter.matcher( text );
    final boolean found = matcher.find( indexFrom );
    if ( !found ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NOT_FOUND_VALUE );
    }

    final int i = matcher.start();
    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( i + 1 ) );
  }

  public String getCanonicalName() {
    return "SEARCH";
  }

}
