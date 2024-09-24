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
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * This function returns text where an old text is substituted with a new text.
 *
 * @author Cedric Pronzato
 */
public class ReplaceFunction implements Function {
  private static final long serialVersionUID = -7678830657739807780L;

  public ReplaceFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type newTextType = parameters.getType( 3 );
    final Object newTextValue = parameters.getValue( 3 );
    final Type textType = parameters.getType( 0 );
    final Object textValue = parameters.getValue( 0 );
    final Type startType = parameters.getType( 1 );
    final Object startValue = parameters.getValue( 1 );
    final Type lengthType = parameters.getType( 2 );
    final Object lengthValue = parameters.getValue( 2 );

    final String newText = typeRegistry.convertToText( newTextType, newTextValue );
    final String text = typeRegistry.convertToText( textType, textValue );
    final Number start = typeRegistry.convertToNumber( startType, startValue );
    if ( start.intValue() <= 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final Number length = typeRegistry.convertToNumber( lengthType, lengthValue );
    if ( length.intValue() < 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final String result1 = MidFunction.process( text, 1, ( start.intValue() - 1 ) );
    final String result2 = MidFunction.process( text, ( start.intValue() + length.intValue() ), ( text.length() ) );

    final StringBuffer buffer = new StringBuffer( result1.length() + newText.length() + result2.length() );
    buffer.append( result1 );
    buffer.append( newText );
    buffer.append( result2 );
    return new TypeValuePair( TextType.TYPE, buffer.toString() );
  }

  public String getCanonicalName() {
    return "REPLACE";
  }

}
