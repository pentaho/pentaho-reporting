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

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.typing.NumberSequence;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.sequence.DefaultNumberSequence;

import java.math.BigDecimal;

/**
 * Creation-Date: 31.10.2006, 17:39:19
 *
 * @author Thomas Morgner
 */
public class SumAFunction extends SumFunction {

  public SumAFunction() {
  }

  @Override
  public String getCanonicalName() {
    return "SUMA";
  }

  @Override
  protected boolean isStrictSequenceNeeded() {
    return false;
  }

  @Override
  protected NumberSequence convertToNumberSequence( final FormulaContext context, final ParameterCallback parameters,
                                                    int paramIdx )
    throws EvaluationException {

    try {
      return super.convertToNumberSequence( context, parameters, paramIdx );
    } catch ( EvaluationException e ) {
      // Re throw the exception if an NA was found!
      if ( LibFormulaErrorValue.ERROR_NA == e.getErrorValue().getErrorCode() ) {
        throw e;
      }
    }

    // So, no auto conversion possible!
    Type type = parameters.getRaw( paramIdx ).getValueType();
    if ( type.isFlagSet( Type.TEXT_TYPE ) ) {
      return new DefaultNumberSequence( new StaticValue( BigDecimal.ZERO, NumberType.GENERIC_NUMBER ), context );
    } else if ( type.isFlagSet( Type.LOGICAL_TYPE ) ) {
      Boolean value = (Boolean) parameters.getRaw( paramIdx ).evaluate().getValue();
      return new DefaultNumberSequence(
        new StaticValue( ( value ) ? BigDecimal.ONE : BigDecimal.ZERO, NumberType.GENERIC_NUMBER ), context );
    }

    return new DefaultNumberSequence( context );
  }
}
