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

import java.math.BigDecimal;

/**
 * Creation-Date: 10.04.2007, 15:02:39
 *
 * @author Thomas Morgner
 */
public abstract class AbstractNumericOperator implements InfixOperator {
  protected static final Number ZERO = new BigDecimal( 0.0 );
  private static final long serialVersionUID = -1087959445157130705L;

  protected AbstractNumericOperator() {
  }

  public final TypeValuePair evaluate( final FormulaContext context,
                                       final TypeValuePair value1,
                                       final TypeValuePair value2 )
    throws EvaluationException {
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    if ( value1 == null || value2 == null ) {
      // If this happens, then one of the implementations has messed up.
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }

    final Object raw1 = value1.getValue();
    final Object raw2 = value2.getValue();
    if ( raw1 == null || raw2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final Number number1 = convertToNumber( typeRegistry, value1.getType(), raw1, ZERO );
    final Number number2 = convertToNumber( typeRegistry, value2.getType(), raw2, ZERO );
    return new TypeValuePair( NumberType.GENERIC_NUMBER, evaluate( number1, number2 ) );
  }

  protected abstract Number evaluate( final Number number1, final Number number2 ) throws EvaluationException;

  private static Number convertToNumber( final TypeRegistry registry,
                                         final Type type,
                                         final Object value,
                                         final Number defaultValue ) {
    if ( value == null ) {
      return defaultValue;
    }
    try {
      return registry.convertToNumber( type, value );
    } catch ( EvaluationException e ) {
      return defaultValue;
    }
  }

}
