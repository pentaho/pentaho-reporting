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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;

/**
 * This function counts the number of blank cells in the Reference provided. A cell is blank if it cell is empty.
 *
 * @author Cedric Pronzato
 */
// todo: maybe use something else than sequence (use array instead because sequence allows the use of scalar)
public class CountBlankFunction implements Function {

  public CountBlankFunction() {
  }

  public String getCanonicalName() {
    return "COUNTBLANK";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    int count = 0;

    final Object value = parameters.getValue( 0 );
    final Type type = parameters.getType( 0 );
    final LValue raw = parameters.getRaw( 0 );

    if ( raw instanceof ContextLookup ) {
      if ( value != null ) {
        try {
          final Sequence sequence = context.getTypeRegistry().convertToSequence( type, value );

          while ( sequence.hasNext() ) {
            final Object o = sequence.next();
            if ( o == null ) {
              count++;
            }
          }
        } catch ( EvaluationException e ) {

        }
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( count ) );
  }
}
