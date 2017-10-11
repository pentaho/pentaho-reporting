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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class ParseDateFunction implements Function {
  public ParseDateFunction() {
  }

  public String getCanonicalName() {
    return "PARSEDATE";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() < 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    if ( parameters.getParameterCount() > 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final String dateString = typeRegistry.convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
    final String pattern = typeRegistry.convertToText( parameters.getType( 1 ), parameters.getValue( 1 ) );
    final Locale locale;
    if ( parameters.getParameterCount() > 2 ) {
      final String localeText = typeRegistry.convertToText( parameters.getType( 2 ), parameters.getValue( 2 ) );
      if ( StringUtils.isEmpty( localeText ) ) {
        locale = context.getLocalizationContext().getLocale();
      } else {
        locale = parseLocale( localeText );
      }
    } else {
      locale = context.getLocalizationContext().getLocale();
    }

    final TimeZone timeZone;
    if ( parameters.getParameterCount() > 3 ) {
      final String timeZoneText = typeRegistry.convertToText( parameters.getType( 3 ), parameters.getValue( 3 ) );
      timeZone = TimeZone.getTimeZone( timeZoneText );
    } else {
      timeZone = context.getLocalizationContext().getTimeZone();
    }

    try {
      final SimpleDateFormat sdf = new SimpleDateFormat( pattern, locale );
      sdf.setTimeZone( timeZone );
      sdf.setLenient( false );
      return new TypeValuePair( DateTimeType.DATETIME_TYPE, sdf.parse( dateString ) );
    } catch ( Exception e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
  }

  private Locale parseLocale( final String s ) throws EvaluationException {
    final StringTokenizer strtok = new StringTokenizer( s.trim(), "_" );
    if ( strtok.hasMoreElements() == false ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
    final String language = strtok.nextToken();
    String country = "";
    if ( strtok.hasMoreTokens() ) {
      country = strtok.nextToken();
    }
    String variant = "";
    if ( strtok.hasMoreTokens() ) {
      variant = strtok.nextToken();
    }
    return new Locale( language, country, variant );
  }
}
