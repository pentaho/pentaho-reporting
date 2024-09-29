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
