/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.text.NumberFormat;

/**
 * This function returns the given value as text.
 *
 * @author Cedric Pronzato
 */
public class FixedFunction implements Function {
  private static final long serialVersionUID = 3505313019941429911L;

  public FixedFunction() {
    // To avoid squid:S1186
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final Type type1 = parameters.getType( 0 );
    final Object value1 = parameters.getValue( 0 );
    final Number result = context.getTypeRegistry().convertToNumber( type1, value1 );
    if ( result == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final NumberFormat currencyInstance =
      NumberFormat.getNumberInstance( context.getLocalizationContext().getLocale() );

    if ( parameterCount >= 2 ) {
      final Type typeDecimals = parameters.getType( 1 );
      final Object valueDecimals = parameters.getValue( 1 );
      final Number resultDecimals = context.getTypeRegistry().convertToNumber( typeDecimals, valueDecimals );
      if ( resultDecimals == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      currencyInstance.setMaximumFractionDigits( resultDecimals.intValue() );
      currencyInstance.setMinimumFractionDigits( resultDecimals.intValue() );
    }

    if ( parameterCount == 3 ) {
      final Type typeOmitSeparators = parameters.getType( 2 );
      final Object valueOmitSeparators = parameters.getValue( 2 );
      final Boolean resultOmitSeparators =
        context.getTypeRegistry().convertToLogical( typeOmitSeparators, valueOmitSeparators );
      if ( resultOmitSeparators == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      currencyInstance.setGroupingUsed( !Boolean.TRUE.equals( resultOmitSeparators ) );
    }
    return new TypeValuePair( TextType.TYPE, currencyInstance.format( result ) );
  }

  public String getCanonicalName() {
    return "FIXED";
  }
}
