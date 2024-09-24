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
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This function returns the number of years, months, or days between two date numbers.<br/>
 * <p/>
 * The Format is a code from the following table, entered as text, that specifies the format you want: <TABLE> <TR>
 * <TH>format</TH> <TH>Returns the number of</TH> </TR> <TR> <TD>y</TD> <TD>Years</TD> </TR> <TR> <TD>m</TD> <TD>Months.
 * If there is not a complete month between the dates, 0 will be returned.</TD> </TR> <TR> <TD>d</TD> <TD>Days</TD>
 * </TR> <TR> <TD>md</TD> <TD>Days, ignoring months and years</TD> </TR> <TR> <TD>ym</TD> <TD>Months, ignoring
 * years</TD> </TR> <TR> <TD>yd</TD> <TD>Days, ignoring years</TD> </TR> <TR> <TD></TD> <TD></TD> </TR> </TABLE>
 *
 * @author Cedric Pronzato
 */
public class DateDifFunction implements Function {
  public static final String YEARS_CODE = "y";
  public static final String MONTHS_CODE = "m";
  public static final String DAYS_CODE = "d";
  public static final String DAYS_IGNORING_YEARS = "yd";
  public static final String MONTHS_IGNORING_YEARS = "ym";
  public static final String DAYS_IGNORING_MONTHS_YEARS = "md";
  private static final long serialVersionUID = 81013707499607068L;

  public DateDifFunction() {
  }

  public String getCanonicalName() {
    return "DATEDIF"; // NON-NLS
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    if ( parameters.getParameterCount() != 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final String formatCode = typeRegistry.convertToText
      ( parameters.getType( 2 ), parameters.getValue( 2 ) );

    if ( formatCode == null || "".equals( formatCode ) ) {
      throw EvaluationException.getInstance(
        LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    long days = computeDays( parameters, typeRegistry );

    if ( DateDifFunction.DAYS_CODE.equals( formatCode ) ) {
      return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( days ) );
    }

    final Date date1 = typeRegistry.convertToDate( parameters.getType( 0 ), parameters.getValue( 0 ) );
    final Date date2 = typeRegistry.convertToDate( parameters.getType( 1 ), parameters.getValue( 1 ) );

    if ( date1 == null || date2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final LocalizationContext localizationContext = context.getLocalizationContext();
    final TimeZone timeZone = localizationContext.getTimeZone();
    final Locale locale = localizationContext.getLocale();
    final GregorianCalendar calandar1 = new GregorianCalendar( timeZone, locale );
    calandar1.setTime( min( date1, date2 ) );

    final GregorianCalendar calandar2 = new GregorianCalendar( timeZone, locale );
    calandar2.setTime( max( date1, date2 ) );

    int sign = ( date1.getTime() < date2.getTime() ) ? 1 : -1;
    final long res = sign * computeDateDifference( formatCode, calandar1, calandar2, days );

    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( res ) );
  }

  protected long computeDays( final ParameterCallback parameters,
                              final TypeRegistry typeRegistry ) throws EvaluationException {
    final Number date1 = typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) );
    final Number date2 = typeRegistry.convertToNumber( parameters.getType( 1 ), parameters.getValue( 1 ) );

    final BigDecimal dn1 = NumberUtil.performIntRounding( NumberUtil.getAsBigDecimal( date1 ) );
    final BigDecimal dn2 = NumberUtil.performIntRounding( NumberUtil.getAsBigDecimal( date2 ) );
    return dn2.longValue() - dn1.longValue();
  }

  protected long computeDateDifference( final String formatCode,
                                        final GregorianCalendar min,
                                        final GregorianCalendar max,
                                        final long days ) throws EvaluationException {
    if ( DateDifFunction.YEARS_CODE.equals( formatCode ) ) {
      // done
      return computeYears( min, max );
    } else if ( DateDifFunction.MONTHS_CODE.equals( formatCode ) ) {
      // done
      return computeMonths( min, max );
    } else if ( DateDifFunction.DAYS_IGNORING_MONTHS_YEARS.equals( formatCode ) ) {
      return computeMonthDays( min, max );

    } else if ( DateDifFunction.MONTHS_IGNORING_YEARS.equals( formatCode ) ) {
      // done
      return computeYearMonth( min, max );
    } else if ( DateDifFunction.DAYS_IGNORING_YEARS.equals( formatCode ) ) {
      // done
      return computeYearDays( min, max, days );
    } else {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
  }

  private long computeYearDays( final GregorianCalendar min, final GregorianCalendar max, final long dayDiff ) {
    final int year1 = min.get( Calendar.YEAR );
    final int year2 = max.get( Calendar.YEAR );
    if ( year1 == year2 ) {
      // simple case: We are within the same year
      return Math.abs( dayDiff );
    }

    final int dayMinDate = min.get( Calendar.DAY_OF_YEAR );
    final int dayMaxDate = max.get( Calendar.DAY_OF_YEAR );
    if ( dayMinDate <= dayMaxDate ) {
      return dayMaxDate - dayMinDate;
    }

    int daysInMinYear = min.getActualMaximum( Calendar.DAY_OF_YEAR );
    int daysToEndOfYear = daysInMinYear - dayMinDate;
    return dayMaxDate + daysToEndOfYear;
  }


  private long computeYearMonth( final GregorianCalendar min, final GregorianCalendar max ) {
    return computeMonths( min, max ) % 12;
  }

  private long computeMonthDays( final GregorianCalendar min, final GregorianCalendar max ) {
    // The number of days between Date1 and Date2, as if Date1 and
    // Date2 were in the same month and the same year.
    int dayMin = min.get( Calendar.DAY_OF_MONTH );
    int dayMax = max.get( Calendar.DAY_OF_MONTH );
    if ( dayMin <= dayMax ) {
      return dayMax - dayMin;
    }

    int maxDaysInMonth = max.getActualMaximum( Calendar.DAY_OF_MONTH );
    return maxDaysInMonth + dayMax - dayMin;
  }

  private int addFieldLoop( final GregorianCalendar c, final GregorianCalendar target, final int field ) {
    c.set( Calendar.MILLISECOND, 0 );
    c.set( Calendar.SECOND, 0 );
    c.set( Calendar.MINUTE, 0 );
    c.set( Calendar.HOUR_OF_DAY, 0 );

    target.set( Calendar.MILLISECOND, 0 );
    target.set( Calendar.SECOND, 0 );
    target.set( Calendar.MINUTE, 0 );
    target.set( Calendar.HOUR_OF_DAY, 0 );

    if ( c.getTimeInMillis() == target.getTimeInMillis() ) {
      return 0;
    }

    int count = 0;
    while ( true ) {
      c.add( field, 1 );
      if ( c.getTimeInMillis() > target.getTimeInMillis() ) {
        return count;
      }
      count += 1;
    }
  }

  private long computeMonths( final GregorianCalendar min, final GregorianCalendar max ) {
    return addFieldLoop( min, max, Calendar.MONTH );
  }

  private long computeYears( final GregorianCalendar min, final GregorianCalendar max ) {
    return addFieldLoop( min, max, Calendar.YEAR );
  }

  private Date min( final Date d1, final Date d2 ) {
    if ( d1.getTime() < d2.getTime() ) {
      return d1;
    }
    return d2;
  }

  private Date max( final Date d1, final Date d2 ) {
    if ( d1.getTime() >= d2.getTime() ) {
      return d1;
    }
    return d2;
  }
}
