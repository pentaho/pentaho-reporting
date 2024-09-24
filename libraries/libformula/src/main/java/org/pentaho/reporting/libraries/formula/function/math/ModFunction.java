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

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function returns the remainder when one number is divided by another number.
 *
 * @author Cedric Pronzato
 */
public class ModFunction implements Function {
  private static final long serialVersionUID = -2492279311353854670L;

  public ModFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final Number number1 = typeRegistry.convertToNumber( type1, value1 );
    if ( number1 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final BigDecimal divided = NumberUtil.getAsBigDecimal( number1 );

    final Type type2 = parameters.getType( 1 );
    final Object value2 = parameters.getValue( 1 );
    final Number number2 = typeRegistry.convertToNumber( type2, value2 );
    if ( number2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final BigDecimal divisor = NumberUtil.getAsBigDecimal( number2 );
    if ( divisor.signum() == 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE );
    }

    final BigDecimal divide = new BigDecimal( divided.divide( divisor, 0, BigDecimal.ROUND_FLOOR ).toString() );
    BigDecimal reminder = divided.subtract( divisor.multiply( divide ) );
    if ( divide.signum() == 0 ) {
      if ( ( divided.signum() == -1 && divisor.signum() != -1 ) || ( divisor.signum() == -1
        && divided.signum() != -1 ) ) {
        reminder = divided.add( divisor );
      }
    }

    return new TypeValuePair( NumberType.GENERIC_NUMBER, reminder );
  }

  public String getCanonicalName() {
    return "MOD";
  }

}
