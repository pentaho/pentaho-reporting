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
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PrevWeekdayFunction implements Function {
  private static final long serialVersionUID = -456924288664083206L;

  public PrevWeekdayFunction() {
  }

  public String getCanonicalName() {
    return "PREVWEEKDAY";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    if ( parameters.getParameterCount() > 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();

    int type = 1; // default is Type 1
    if ( parameters.getParameterCount() == 1 ) {
      final Number n = typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) );
      if ( n == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      type = n.intValue();
      if ( type < 1 || type > 2 ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
    }

    final LocalizationContext localizationContext = context.getLocalizationContext();
    final Date prevweekday = prevweekday( type, localizationContext );

    final Date date = DateUtil.normalizeDate( prevweekday, DateTimeType.DATE_TYPE );
    return new TypeValuePair( DateTimeType.DATE_TYPE, date );
  }

  private static Date prevweekday( final int type, final LocalizationContext context ) {
    final GregorianCalendar gc = new GregorianCalendar( context.getTimeZone(),
      context.getLocale() );
    gc.set( Calendar.MILLISECOND, 0 );
    final int dayOfWeek = gc.get( Calendar.DAY_OF_WEEK );
    if ( type == 1 ) { /*  weekend = saturday + sunday */
      if ( dayOfWeek == Calendar.SUNDAY ) {
        gc.add( Calendar.DAY_OF_MONTH, -2 );
      } else if ( dayOfWeek == Calendar.MONDAY ) {
        gc.add( Calendar.DAY_OF_MONTH, -3 );
      } else {
        gc.add( Calendar.DAY_OF_MONTH, -1 );
      }
    } else { /* weekend = friday + saturday */
      if ( dayOfWeek == Calendar.SATURDAY ) {
        gc.add( Calendar.DAY_OF_MONTH, -2 );
      } else if ( dayOfWeek == Calendar.SUNDAY ) {
        gc.add( Calendar.DAY_OF_MONTH, -3 );
      } else {
        gc.add( Calendar.DAY_OF_MONTH, -1 );
      }
    }

    return gc.getTime();
  }

}
