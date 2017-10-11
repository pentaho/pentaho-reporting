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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;

/**
 * This function counts the numbers in the list of NumberSequences provided. Only numbers in references are counted; all
 * other types are ignored. Errors are not propagated.
 *
 * @author Cedric Pronzato
 */
public class CountFunction implements Function {

  public CountFunction() {
  }

  public String getCanonicalName() {
    return "COUNT";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    int count = 0;

    for ( int paramIdx = 0; paramIdx < parameterCount; paramIdx++ ) {
      try {
        final Type type = parameters.getType( paramIdx );
        final Object value = parameters.getValue( paramIdx );
        final Sequence sequence = context.getTypeRegistry().convertToNumberSequence( type, value, true );

        while ( sequence.hasNext() ) {
          sequence.next();
          count++;
        }
      } catch ( EvaluationException e ) {
        // This is in case of an error value in a scalar argument, we must ignore this error in number sequences
        // todo: maybe it has to be done for other type of exceptions.
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( count ) );
  }
}
