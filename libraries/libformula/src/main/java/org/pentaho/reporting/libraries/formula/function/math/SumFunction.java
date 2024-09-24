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
 * Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.NumberSequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Creation-Date: 31.10.2006, 17:39:19
 *
 * @author Thomas Morgner
 */
public class SumFunction implements Function {

  private static final long serialVersionUID = -8604838130517819412L;

  public SumFunction() {
  }

  public String getCanonicalName() {
    return "SUM";
  }

  protected boolean isStrictSequenceNeeded() {
    return true;
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    BigDecimal computedResult = BigDecimal.ZERO;
    final int parameterCount = parameters.getParameterCount();

    if ( parameterCount == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    for ( int paramIdx = 0; paramIdx < parameterCount; paramIdx++ ) {
      final NumberSequence sequence = convertToNumberSequence( context, parameters, paramIdx );

      while ( sequence.hasNext() ) {
        computedResult = compute( sequence.nextNumber(), computedResult );
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, computedResult );
  }

  protected NumberSequence convertToNumberSequence( final FormulaContext context, final ParameterCallback parameters,
                                                    int paramIdx )
    throws EvaluationException {
    Type type = parameters.getType( paramIdx );
    Object value = parameters.getValue( paramIdx );
    return context.getTypeRegistry().convertToNumberSequence( type, value, isStrictSequenceNeeded() );
  }

  private BigDecimal compute( final Number value,
                              final BigDecimal computedResult ) {
    if ( value == null ) {
      // no-op ..
      return computedResult;
    }

    return computedResult.add( NumberUtil.getAsBigDecimal( value ) );
  }
}
