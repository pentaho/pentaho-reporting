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
