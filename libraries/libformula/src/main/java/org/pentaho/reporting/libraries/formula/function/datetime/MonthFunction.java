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
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * This function extracts the month from a date.
 *
 * @author Cedric Pronzato
 */
public class MonthFunction implements Function {
  private static final long serialVersionUID = 4577299588034323045L;

  public MonthFunction() {
  }

  public String getCanonicalName() {
    return "MONTH";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Date d = typeRegistry.convertToDate( parameters.getType( 0 ), parameters.getValue( 0 ) );

    if ( d == null ) {
      throw EvaluationException.getInstance(
        LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final Calendar gc = DateUtil.createCalendar( d, context.getLocalizationContext() );

    final int month = gc.get( Calendar.MONTH ) + 1;
    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( month ) );
  }
}
