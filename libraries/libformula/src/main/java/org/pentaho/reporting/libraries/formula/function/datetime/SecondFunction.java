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


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function extracts the minute (0 through 59) from a time.
 *
 * @author Cedric Pronzato
 */
public class SecondFunction implements Function {
  private static final BigDecimal MINUTES_PER_DAY = new BigDecimal( 24 * 60.0 );
  private static final BigDecimal SECONDS = new BigDecimal( 60.0 );

  public SecondFunction() {
  }

  public String getCanonicalName() {
    return "SECOND";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Number n = typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) );

    if ( n == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    // calculation is as follows

    // time * 24 so that we get the full hours (which we remove later)
    final BigDecimal bd = NumberUtil.getAsBigDecimal( n );
    final BigDecimal hours = bd.multiply( MINUTES_PER_DAY );
    final BigDecimal dayAndHoursAsInt = NumberUtil.performIntRounding( hours );
    final BigDecimal minutesFraction = hours.subtract( dayAndHoursAsInt );

    // Multiply the minutes with 60 to get the minutes as ints
    final BigDecimal seconds = minutesFraction.multiply( SECONDS );
    final BigDecimal secondsAsInt = NumberUtil.performIntRounding( seconds );

    return new TypeValuePair( NumberType.GENERIC_NUMBER, secondsAsInt );
  }
}
