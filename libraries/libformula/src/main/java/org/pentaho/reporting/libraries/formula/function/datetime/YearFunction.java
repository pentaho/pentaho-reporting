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
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.util.DateUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * This function extracts the year from a date.
 *
 * @author Cedric Pronzato
 */
public class YearFunction implements Function {
  private static final long serialVersionUID = 2486417585939551783L;

  public YearFunction() {
  }

  public String getCanonicalName() {
    return "YEAR";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();

    final Date d = typeRegistry.convertToDate( parameters.getType( 0 ), parameters.getValue( 0 ) );

    if ( d == null ) {
      throw EvaluationException.getInstance(
        LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    final Calendar gc = DateUtil.createCalendar( d, context.getLocalizationContext() );
    final int year = gc.get( Calendar.YEAR );
    //noinspection UnpredictableBigDecimalConstructorCall
    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( (double) year ) );
  }
}
