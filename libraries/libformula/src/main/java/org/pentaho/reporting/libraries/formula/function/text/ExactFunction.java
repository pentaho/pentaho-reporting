/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
