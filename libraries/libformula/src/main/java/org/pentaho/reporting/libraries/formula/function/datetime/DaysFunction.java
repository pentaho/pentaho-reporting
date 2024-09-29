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
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This is the same as DATEDIFF(date_1, date_2, "d");
 *
 * @author Thomas Morgner
 */
public class DaysFunction implements Function {
  public DaysFunction() {
  }

  public String getCanonicalName() {
    return "DAYS";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    if ( parameters.getParameterCount() != 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Date date1 = typeRegistry.convertToDate
      ( parameters.getType( 0 ), parameters.getValue( 0 ) );
    final Date date2 = typeRegistry.convertToDate
      ( parameters.getType( 1 ), parameters.getValue( 1 ) );

    if ( date1 == null || date2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final LocalizationContext localizationContext = context.getLocalizationContext();
    final TimeZone timeZone = localizationContext.getTimeZone();
    final Locale locale = localizationContext.getLocale();
    final GregorianCalendar calandar1 =
      new GregorianCalendar( timeZone, locale );
    calandar1.setTime( date1 );

    final GregorianCalendar calandar2 =
      new GregorianCalendar( timeZone, locale );
    calandar2.setTime( date2 );

    //calculate the difference in millis and divide it with no. of millis in a day
    //This will fetch us no of days
    long res = (calandar2.getTimeInMillis() - calandar1.getTimeInMillis()) / (1000 * 60 * 60 * 24);
    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( (double) res ) );
  }
}
