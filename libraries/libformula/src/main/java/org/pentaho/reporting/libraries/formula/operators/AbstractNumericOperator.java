/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
