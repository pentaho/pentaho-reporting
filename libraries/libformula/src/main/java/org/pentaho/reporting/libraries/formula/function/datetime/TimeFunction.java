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
