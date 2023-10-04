/*!
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
* Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
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
