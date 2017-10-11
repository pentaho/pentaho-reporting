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

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

import java.util.ArrayList;

public class NormalizeArrayFunction implements Function {
  public NormalizeArrayFunction() {
  }

  public String getCanonicalName() {
    return "NORMALIZEARRAY";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount == 0 || parameterCount > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Sequence sequenceRaw =
      context.getTypeRegistry().convertToSequence( parameters.getType( 0 ), parameters.getValue( 0 ) );
    if ( sequenceRaw == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }
    final RecursiveSequence sequence = new RecursiveSequence( sequenceRaw, context );

    final boolean removeNull;
    if ( parameterCount == 2 ) {
      final Boolean removeNullValuesRaw =
        context.getTypeRegistry().convertToLogical( parameters.getType( 1 ), parameters.getValue( 1 ) );
      removeNull = removeNullValuesRaw == null || Boolean.TRUE.equals( removeNullValuesRaw );
    } else {
      removeNull = true;
    }

    final ArrayList retval = new ArrayList();
    while ( sequence.hasNext() ) {
      final Object o = sequence.next();
      if ( removeNull && o == null ) {
        continue;
      }
      retval.add( o );
    }
    return new TypeValuePair( AnyType.ANY_ARRAY, retval.toArray() );
  }
}
