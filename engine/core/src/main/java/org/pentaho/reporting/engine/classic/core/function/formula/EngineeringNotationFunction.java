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

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Function to turn a big number into a readable format using engineering notation as described
 * http://en.wikipedia.org/wiki/Engineering_notation
 *
 * @author Pedro Alves.
 */
public class EngineeringNotationFunction implements Function {
  private static final String[] SUFFIXES = { "y", "z", "a", "f", "p", "n", "\u00B5", "m", " ", "k", "M", "G", "T", "P",
    "E", "Z", "Y" };
  private static final int OFFSET = 8;

  public EngineeringNotationFunction() {
  }

  public String getCanonicalName() {
    return "ENGINEERINGNOTATION";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {

    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final double value =
        typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) ).doubleValue();

    if ( value == 0 ) {
      return new TypeValuePair( TextType.TYPE, "0 " );
    }

    boolean fixedSize = false;
    int precision = 0;

    if ( parameters.getParameterCount() > 1 ) {
      fixedSize = true;
      precision = typeRegistry.convertToNumber( parameters.getType( 1 ), parameters.getValue( 1 ) ).intValue();
      if ( parameters.getParameterCount() == 3 ) {
        final Boolean rawFixedSize = typeRegistry.convertToLogical( parameters.getType( 2 ), parameters.getValue( 2 ) );
        fixedSize = rawFixedSize.booleanValue();
      }
    }

    final int log10 = computeLog10( value );

    // index will allow us to find the the index of the suffix to use
    final int index = (int) ( Math.floor( log10 / 3.0 ) + OFFSET );
    if ( index < 0 || index >= SUFFIXES.length ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }

    // Find the adequate precision. % operator behaves badly in negative results, so we need to make it work as expected
    final int roundPrecision = fixedSize ? ( log10 - precision ) : ( log10 - ( precision + ( 3 + log10 % 3 ) % 3 ) );

    // Round the value
    final double roundingScale = Math.pow( 10, roundPrecision );
    final double rounded = Math.round( value / roundingScale ) * roundingScale;

    // Get it's eng format. Get it as string without trailing 0's
    final double outputValue = rounded / Math.pow( 10, Math.floor( log10 / 3.0 ) * 3 );
    final int outputValueDecimalPlaces = Math.max( 1, computeLog10( outputValue ) );

    final Locale locale = context.getLocalizationContext().getLocale();
    final NumberFormat decimalFormat = createDecimalFormat( fixedSize, outputValueDecimalPlaces, precision, locale );
    final String result = decimalFormat.format( outputValue ) + SUFFIXES[index];
    return new TypeValuePair( TextType.TYPE, result );
  }

  private NumberFormat createDecimalFormat( final boolean fixedSize, final int decimalPlaces, final int precision,
      final Locale locale ) {
    final NumberFormat format = NumberFormat.getNumberInstance( locale );
    format.setGroupingUsed( false );
    if ( fixedSize ) {
      format.setMinimumFractionDigits( Math.max( 0, precision - decimalPlaces - 1 ) );
    } else {
      format.setMinimumFractionDigits( precision );
    }
    return format;
  }

  private int computeLog10( final double value ) {

    final boolean inverted;
    double analysis = Math.abs( value );

    if ( analysis < 1 ) {
      analysis = 1 / analysis;
      inverted = true;
    } else {
      inverted = false;
    }

    int log = 0;
    while ( analysis >= 10 ) {
      log++;
      analysis = analysis / 10;
    }

    return inverted ? -log - 1 : log;
  }
}
