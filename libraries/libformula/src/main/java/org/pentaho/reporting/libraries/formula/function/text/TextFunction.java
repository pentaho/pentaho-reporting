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

package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This function returns the given value as text.
 *
 * @author Cedric Pronzato
 */
public class TextFunction implements Function {
  private static final long serialVersionUID = 3505313019941429911L;

  public TextFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    if ( parameterCount == 1 ) {
      final String result = typeRegistry.convertToText( type1, value1 );
      if ( result == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }

      return new TypeValuePair( TextType.TYPE, result );
    }

    try {
      final Type type2 = parameters.getType( 1 );
      final Object value2 = parameters.getValue( 1 );
      final String formatString = typeRegistry.convertToText( type2, value2 );
      final Locale locale = context.getLocalizationContext().getLocale();
      if ( value1 instanceof Date ) {
        final DateFormat dateFormat = new SimpleDateFormat
          ( formatString, new DateFormatSymbols( locale ) );
        return new TypeValuePair( TextType.TYPE, dateFormat.format( value1 ) );
      }
      if ( value1 instanceof Number ) {
        final NumberFormat dateFormat = new DecimalFormat
          ( formatString, new DecimalFormatSymbols( locale ) );
        return new TypeValuePair( TextType.TYPE, dateFormat.format( value1 ) );
      }
      try {
        final Date date = typeRegistry.convertToDate( type2, value2 );
        final DateFormat dateFormat = new SimpleDateFormat
          ( formatString, new DateFormatSymbols( locale ) );
        return new TypeValuePair( TextType.TYPE, dateFormat.format( date ) );
      } catch ( EvaluationException ev ) {
        // ignore
      }
      try {
        final Number date = typeRegistry.convertToNumber( type2, value2 );
        final NumberFormat dateFormat = new DecimalFormat
          ( formatString, new DecimalFormatSymbols( locale ) );
        return new TypeValuePair( TextType.TYPE, dateFormat.format( date ) );
      } catch ( EvaluationException ev ) {
        // ignore
      }
    } catch ( EvaluationException ev ) {
      throw ev;
    } catch ( Exception ex ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }

    throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
  }

  public String getCanonicalName() {
    return "TEXT";
  }

}
