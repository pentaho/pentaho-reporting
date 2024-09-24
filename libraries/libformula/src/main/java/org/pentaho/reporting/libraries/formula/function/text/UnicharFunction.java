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
* Copyright (c) 2006 - 2023 Hitachi Vantara and Contributors.  All rights reserved.
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

/**
 * This function returns the given value as text.
 *
 * @author Cedric Pronzato
 */
public class UnicharFunction implements Function {
  private static final long serialVersionUID = 3505313019941429911L;
  private static final int maxUniCode = 1114111;
  private static final int minUniCode = 1;

  public UnicharFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final Number result = context.getTypeRegistry().convertToNumber( type1, value1 );

    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final int value = result.intValue();

    // Check if the Unicode codepoint value lies between 1 and 1114111.
    if ( value > maxUniCode || value < minUniCode ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    // To Handle Unicode codepoint even if it is larger than 65535
    // which (char) cannot properly handle, so using toChars().
    return new TypeValuePair( TextType.TYPE, String.valueOf( Character.toChars( result.intValue() ) ) );
  }

  public String getCanonicalName() {
    return "UNICHAR";
  }

}
