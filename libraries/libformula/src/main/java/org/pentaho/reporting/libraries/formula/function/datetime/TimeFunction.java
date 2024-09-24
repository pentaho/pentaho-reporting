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
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.sql.Time;

/**
 * This fonction constructs a time from hours, minutes, and seconds.
 *
 * @author Cedric Pronzato
 */
public class TimeFunction implements Function {
  private static final long serialVersionUID = -9175775325047486483L;

  public TimeFunction() {
  }

  public String getCanonicalName() {
    return "TIME";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Number n1;
    final Number n2;
    final Number n3;
    try {
      final TypeRegistry typeRegistry = context.getTypeRegistry();
      n1 = typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) );
      n2 = typeRegistry.convertToNumber( parameters.getType( 1 ), parameters.getValue( 1 ) );
      n3 = typeRegistry.convertToNumber( parameters.getType( 2 ), parameters.getValue( 2 ) );
    } catch ( NumberFormatException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    if ( n1 == null || n2 == null || n3 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    final int hours = n1.intValue();
    final int minutes = n2.intValue();
    final int seconds = n3.intValue();

    final LocalizationContext localizationContext = context.getLocalizationContext();
    final Time time = DateUtil.createTime( hours, minutes, seconds, localizationContext );
    return new TypeValuePair( DateTimeType.TIME_TYPE, time );
  }
}
