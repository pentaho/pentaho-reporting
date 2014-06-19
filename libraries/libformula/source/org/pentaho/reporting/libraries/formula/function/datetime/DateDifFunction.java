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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.datetime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.math.BigDecimal;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

/**
 * This function returns the number of years, months, or days between two date
 * numbers.<br/>
 * <p/>
 * The Format is a code from the following table, entered as text, that
 * specifies the format you want: <TABLE> <TR> <TH>format</TH> <TH>Returns the
 * number of</TH> </TR> <TR> <TD>y</TD> <TD>Years</TD> </TR> <TR> <TD>m</TD>
 * <TD>Months. If there is not a complete month between the dates, 0 will be
 * returned.</TD> </TR> <TR> <TD>d</TD> <TD>Days</TD> </TR> <TR> <TD>md</TD>
 * <TD>Days, ignoring months and years</TD> </TR> <TR> <TD>ym</TD> <TD>Months,
 * ignoring years</TD> </TR> <TR> <TD>yd</TD> <TD>Days, ignoring years</TD>
 * </TR> <TR> <TD></TD> <TD></TD> </TR> </TABLE>
 *
 * @author Cedric Pronzato
 */
public class DateDifFunction implements Function
{
  public static final String YEARS_CODE = "y";
  public static final String MONTHS_CODE = "m";
  public static final String DAYS_CODE = "d";
  public static final String DAYS_IGNORING_YEARS = "yd";
  public static final String MONTHS_IGNORING_YEARS = "ym";
  public static final String DAYS_IGNORING_MONTHS_YEARS = "md";
  private static final long serialVersionUID = 81013707499607068L;

  public DateDifFunction()
  {
  }

  public String getCanonicalName()
  {
    return "DATEDIF";
  }

  public TypeValuePair evaluate(final FormulaContext context,
                                final ParameterCallback parameters)
      throws EvaluationException
  {
    if (parameters.getParameterCount() != 3)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final String formatCode = typeRegistry.convertToText
        (parameters.getType(2), parameters.getValue(2));

    if (formatCode == null || "".equals(formatCode))
    {
      throw EvaluationException.getInstance(
          LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }

    if (DateDifFunction.DAYS_CODE.equals(formatCode))
    {
      final Number date1 = typeRegistry.convertToNumber
          (parameters.getType(0), parameters.getValue(0));
      final Number date2 = typeRegistry.convertToNumber
          (parameters.getType(1), parameters.getValue(1));

      final BigDecimal dn1 = NumberUtil.performIntRounding(NumberUtil.getAsBigDecimal(date1));
      final BigDecimal dn2 = NumberUtil.performIntRounding(NumberUtil.getAsBigDecimal(date2));
      //noinspection UnpredictableBigDecimalConstructorCall
      return new TypeValuePair(NumberType.GENERIC_NUMBER, new BigDecimal(dn2.longValue() - dn1.longValue()));
    }

    final Date date1 = typeRegistry.convertToDate
        (parameters.getType(0), parameters.getValue(0));
    final Date date2 = typeRegistry.convertToDate
        (parameters.getType(1), parameters.getValue(1));

    if (date1 == null || date2 == null)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }

    final LocalizationContext localizationContext = context.getLocalizationContext();
    final TimeZone timeZone = localizationContext.getTimeZone();
    final Locale locale = localizationContext.getLocale();
    final GregorianCalendar calandar1 =
        new GregorianCalendar(timeZone, locale);
    calandar1.setTime(date1);

    final GregorianCalendar calandar2 =
        new GregorianCalendar(timeZone, locale);
    calandar2.setTime(date2);

    final int res;

    if (DateDifFunction.YEARS_CODE.equals(formatCode))
    {
      res = Math.abs(calandar2.get(Calendar.YEAR) - calandar1.get(Calendar.YEAR));
    }
    else if (DateDifFunction.MONTHS_CODE.equals(formatCode))
    {
      final int month1 = calandar1.get(Calendar.MONTH);
      final int month2 = calandar2.get(Calendar.MONTH);
      final int year1 = calandar1.get(Calendar.YEAR);
      final int year2 = calandar2.get(Calendar.YEAR);

      res = Math.abs(year2 - year1) * 12 + Math.abs(month2 - month1);
    }
    else if (DateDifFunction.DAYS_IGNORING_MONTHS_YEARS.equals(formatCode))
    {
      // The number of days between Date1 and Date2, as if Date1 and
      // Date2 were in the same month and the same year.

      // Not sure what happens to leap years, so this solution may be invalid.
      calandar1.set(Calendar.YEAR, calandar2.get(Calendar.YEAR));
      calandar1.set(Calendar.MONTH, calandar2.get(Calendar.MONTH));

      res = Math.abs(calandar2.get(Calendar.DAY_OF_MONTH) -
                     calandar1.get(Calendar.DAY_OF_MONTH));
    }
    else if (DateDifFunction.MONTHS_IGNORING_YEARS.equals(formatCode))
    {
      final int month1 = calandar1.get(Calendar.MONTH);
      final int month2 = calandar2.get(Calendar.MONTH);

      res = Math.abs(month2 - month1);
    }
    else if (DateDifFunction.DAYS_IGNORING_YEARS.equals(formatCode))
    {
      //Isn't that a stupid case? How could we count the days while ignoring
      //how much days there are in each months without using the year?

      // The number of days between Date1 and Date2, as if Date1 and Date2
      // were in the same year.

      // Not sure what happens to leap years, so this solution may be invalid.
      calandar1.set(Calendar.YEAR, calandar2.get(Calendar.YEAR));
      final int dayOne = calandar1.get(Calendar.DAY_OF_YEAR);
      final int dayTwo = calandar2.get(Calendar.DAY_OF_YEAR);
      res = Math.abs(dayOne - dayTwo);
    }
    else
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE);
    }

    //noinspection UnpredictableBigDecimalConstructorCall
    return new TypeValuePair(NumberType.GENERIC_NUMBER, new BigDecimal((double) res));
  }
}
