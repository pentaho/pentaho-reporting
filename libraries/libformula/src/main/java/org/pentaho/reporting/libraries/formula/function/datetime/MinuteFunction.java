/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
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
public class MinuteFunction implements Function {
  private static final BigDecimal MINUTES_PER_DAY = new BigDecimal( 24.0 * 60 );
  private static final BigDecimal MINUTES = new BigDecimal( 60.0 );
  private static final BigDecimal HOURS = new BigDecimal( 24.0 );

  public MinuteFunction() {
  }

  public String getCanonicalName() {
    return "MINUTE";
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
    final BigDecimal hours = bd.multiply( HOURS );
    final BigDecimal dayAndHoursAsInt = new BigDecimal( NumberUtil.performIntRounding( hours ).intValue() );
    final BigDecimal minutesFraction = hours.subtract( dayAndHoursAsInt );

    // Multiply the minutes with 60 to get the minutes as ints 
    final BigDecimal minutes = minutesFraction.multiply( MINUTES );
    // Fix for PRD-5499, contributed by Lionel Elie Mamane
    // final BigDecimal minutesAsInt = minutes.setScale( 0, BigDecimal.ROUND_HALF_UP );
    final BigDecimal minutesAsInt = NumberUtil.performMinuteRounding( minutes );
    return new TypeValuePair( NumberType.GENERIC_NUMBER, minutesAsInt );
  }
}
