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


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * This function extracts the hour (0 through 23) from a time.
 *
 * @author Cedric Pronzato
 */
public class HourFunction implements Function {
  private static final BigDecimal HOUR_24 = new BigDecimal( 24.0 );
  private static final long serialVersionUID = 1877256236005061937L;

  public HourFunction() {
  }

  public String getCanonicalName() {
    return "HOUR";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Number n = typeRegistry.convertToNumber( parameters.getType( 0 ), parameters.getValue( 0 ) );

    if ( n == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final BigDecimal bd = NumberUtil.getAsBigDecimal( n );
    final BigDecimal day = new BigDecimal( NumberUtil.performIntRounding( bd ).intValue() );
    final BigDecimal dayFraction = bd.subtract( day );

    final BigDecimal hourAndMinutesVal = dayFraction.multiply( HOUR_24 );
    final BigDecimal hours = NumberUtil.performIntRounding( hourAndMinutesVal );
    return new TypeValuePair( NumberType.GENERIC_NUMBER, hours );
  }
}
