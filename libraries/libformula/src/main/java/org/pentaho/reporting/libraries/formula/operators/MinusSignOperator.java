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

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Creation-Date: 02.11.2006, 10:27:03
 *
 * @author Thomas Morgner
 */
public class MinusSignOperator implements PrefixOperator {
  private static final BigDecimal ZERO = new BigDecimal( 0.0 );
  private static final long serialVersionUID = 7453766552980074751L;


  public MinusSignOperator() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final TypeValuePair value1 )
    throws EvaluationException {
    final Type type = value1.getType();
    final Object val = value1.getValue();
    if ( val == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    if ( type.isFlagSet( Type.NUMERIC_TYPE ) ) {
      final TypeRegistry typeRegistry = context.getTypeRegistry();
      // return the same as zero minus value.
      final Number number = typeRegistry.convertToNumber( type, val );
      if ( number == null ) {
        throw EvaluationException.getInstance
          ( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      final BigDecimal value = NumberUtil.getAsBigDecimal( number );
      return new TypeValuePair( NumberType.GENERIC_NUMBER, ZERO.subtract( value ) );
    }

    if ( val instanceof Number ) {
      final BigDecimal value = NumberUtil.getAsBigDecimal( (Number) val );
      if ( value == null ) {
        throw EvaluationException.getInstance
          ( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      return new TypeValuePair( type, ZERO.subtract( value ) );
    }

    throw EvaluationException.getInstance
      ( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
  }

  public String toString() {
    return "-";
  }
}
