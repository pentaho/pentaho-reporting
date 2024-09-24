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
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.util.Date;

/**
 * This fonction constructs a time from hours, minutes, and seconds.
 *
 * @author Cedric Pronzato
 */
public class TimeValueFunction implements Function {
  private static final long serialVersionUID = -9175775325047486483L;

  public TimeValueFunction() {
  }

  public String getCanonicalName() {
    return "TIMEVALUE";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Type type = parameters.getType( 0 );
    final Object value = parameters.getValue( 0 );

    final Date date1 = typeRegistry.convertToDate( type, value );
    final Date time = DateUtil.normalizeDate( date1, DateTimeType.TIME_TYPE );
    return new TypeValuePair( DateTimeType.TIME_TYPE, time );
  }
}
