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
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class YesterdayFunction implements Function {
  private static final long serialVersionUID = -456933388664083206L;

  public YesterdayFunction() {
  }

  public String getCanonicalName() {
    return "YESTERDAY";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters )
    throws EvaluationException {
    final Date yesterday = yesterday( context );

    final Date date = DateUtil.normalizeDate( yesterday, DateTimeType.DATE_TYPE );
    return new TypeValuePair( DateTimeType.DATE_TYPE, date );
  }

  private static Date yesterday( final FormulaContext context ) {
    final LocalizationContext localizationContext = context.getLocalizationContext();
    final GregorianCalendar gc = new GregorianCalendar( localizationContext.getTimeZone(),
      localizationContext.getLocale() );
    gc.setTime( context.getCurrentDate() );
    gc.set( Calendar.MILLISECOND, 0 );
    gc.add( Calendar.DAY_OF_MONTH, -1 );
    return gc.getTime();
  }

}
