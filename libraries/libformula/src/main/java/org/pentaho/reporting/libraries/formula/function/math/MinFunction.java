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
 * Copyright (c) 2008 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function returns the minimum from a set of numbers.
 *
 * @author Cedric Pronzato
 */
public class MinFunction implements Function {

  private static final long serialVersionUID = 255618510939561419L;

  public MinFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      return new TypeValuePair( NumberType.GENERIC_NUMBER, BigDecimal.ZERO );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    BigDecimal last = null;
    for ( int paramIdx = 0; paramIdx < parameterCount; paramIdx++ ) {
      final Type type = parameters.getType( paramIdx );
      final Object value = parameters.getValue( paramIdx );
      final Sequence sequence = typeRegistry.convertToNumberSequence( type, value, isStrictSequenceNeeded() );

      while ( sequence.hasNext() ) {
        final LValue rawValue = sequence.nextRawValue();
        if ( rawValue == null ) {
          throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
        }
        final TypeValuePair nextValue = rawValue.evaluate();
        final Number number = typeRegistry.convertToNumber( nextValue.getType(), nextValue.getValue() );
        final BigDecimal next = NumberUtil.getAsBigDecimal( number );

        if ( last == null || last.compareTo( next ) > 0 ) {
          last = next;
        }
      }
    }

    if ( last == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, last );
  }

  protected boolean isStrictSequenceNeeded() {
    return true;
  }

  public String getCanonicalName() {
    return "MIN";
  }
}
