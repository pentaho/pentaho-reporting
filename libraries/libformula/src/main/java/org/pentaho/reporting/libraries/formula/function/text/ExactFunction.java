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
import org.pentaho.reporting.libraries.formula.typing.TypeConversionException;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function reports if two given text values are exactly equal using a case-sensitive comparison.
 *
 * @author Cedric Pronzato
 */
public class ExactFunction implements Function {
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final long serialVersionUID = -6303315343568906710L;

  public ExactFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount != 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Type textType1 = parameters.getType( 0 );
    final Object textValue1 = parameters.getValue( 0 );
    final Type textType2 = parameters.getType( 1 );
    final Object textValue2 = parameters.getValue( 1 );


    // Numerical comparisons ignore "trivial" differences that
    // depend only on numeric precision of finite numbers.

    // This fixes the common rounding errors, that are encountered when computing "((1/3) * 3)", which results
    // in 0.99999 and not 1, as expected.
    try {
      final Number number1 = typeRegistry.convertToNumber( textType1, textValue1 );
      final Number number2 = typeRegistry.convertToNumber( textType2, textValue2 );

      final double delta = Math.abs( Math.abs( number1.doubleValue() ) - Math.abs( number2.doubleValue() ) );
      if ( delta < 0.00005 ) {
        return RETURN_TRUE;
      }
      return RETURN_FALSE;
    } catch ( TypeConversionException tce ) {
      // Ignore, try to compare them as strings ..
    }

    final String text1 = typeRegistry.convertToText( textType1, textValue1 );
    final String text2 = typeRegistry.convertToText( textType2, textValue2 );
    if ( text1 == null || text2 == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    if ( text1.equals( text2 ) ) {
      return RETURN_TRUE;
    }
    return RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "EXACT";
  }

}
