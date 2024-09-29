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
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * This function extracts the day of week from a date. <p/> The returned value depends of the Type passed as second
 * argument using the following table:<br/> <TABLE> <TR> <TH>Day of Week</TH> <TH>Type=1 Result</TH> <TH>Type=2
 * Result</TH> <TH>Type=3 Result</TH> </TR> <TR> <TD>Sunday</TD> <TD> 1</TD> <TD> 7</TD> <TD> 6</TD> </TR> <TR>
 * <TD>Monday</TD> <TD> 2</TD> <TD> 1</TD> <TD> 0</TD> </TR> <TR> <TD>Tuesday</TD> <TD> 3</TD> <TD> 2</TD> <TD> 1</TD>
 * </TR> <TR> <TD>Wednesday</TD> <TD> 4</TD> <TD> 3</TD> <TD> 2</TD> </TR> <TR> <TD>Thursday</TD> <TD> 5</TD> <TD>
 * 4</TD> <TD> 3</TD> </TR> <TR> <TD>Friday</TD> <TD> 6</TD> <TD> 5</TD> <TD> 4</TD> </TR> <TR> <TD>Saturday</TD> <TD>
 * 7</TD> <TD> 6</TD> <TD> 5</TD> </TR> </TABLE>
 *
 * @author Cedric Pronzato
 */
public class WeekDayFunction implements Function {
  private static final long serialVersionUID = -825027235225096201L;

  public WeekDayFunction() {
  }

  public String getCanonicalName() {
    return "WEEKDAY";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Date d = typeRegistry.convertToDate( parameters.getType( 0 ), parameters.getValue( 0 ) );
    int type = 1; // default is Type 1
    if ( parameters.getParameterCount() == 2 ) {
      final Number n = typeRegistry.convertToNumber( parameters.getType( 1 ), parameters.getValue( 1 ) );
      if ( n == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      type = n.intValue();
      if ( type < 1 || type > 3 ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
    }

    if ( d == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final Calendar gc = DateUtil.createCalendar( d, context.getLocalizationContext() );

    final int dayOfWeek = gc.get( Calendar.DAY_OF_WEEK );
    // in java Sunday = 1 (= Type 1 of openformula)
    final int result = convertType( dayOfWeek, type );
    //noinspection UnpredictableBigDecimalConstructorCall
    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( (double) result ) );
  }

  public int convertType( final int currentDayOfWeek, final int type ) {
    if ( type == 1 ) {
      return currentDayOfWeek;
    } else if ( type == 2 ) {
      final int i = ( ( currentDayOfWeek + 6 ) % 8 );
      if ( i == 7 ) {
        return i;
      } else {
        return i + 1;
      }
    } else {
      return ( currentDayOfWeek + 5 ) % 7;
    }
  }
}
