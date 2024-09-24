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
