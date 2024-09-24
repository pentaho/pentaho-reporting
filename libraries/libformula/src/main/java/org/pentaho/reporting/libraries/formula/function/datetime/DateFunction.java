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

/**
 * Creation-Date: 04.11.2006, 18:59:11
 *
 * @author Thomas Morgner
 */
public class DateFunction implements Function {
  private static final long serialVersionUID = 4956151361696995668L;

  public DateFunction() {
  }

  public String getCanonicalName() {
    return "DATE";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Number n1 = typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) );
    final Number n2 = typeRegistry.convertToNumber( parameters.getType( 1 ), parameters.getValue( 1 ) );
    final Number n3 = typeRegistry.convertToNumber( parameters.getType( 2 ), parameters.getValue( 2 ) );

    if ( n1 == null || n2 == null || n3 == null ) {
      throw EvaluationException.getInstance(
        LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final LocalizationContext localizationContext = context
      .getLocalizationContext();
    final java.sql.Date date = DateUtil.createDate( n1.intValue(),
      n2.intValue(), n3.intValue(), localizationContext );

    return new TypeValuePair( DateTimeType.DATE_TYPE, date );
  }
}
