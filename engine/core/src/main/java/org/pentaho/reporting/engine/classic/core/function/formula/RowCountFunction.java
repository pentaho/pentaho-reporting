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


package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;

import java.math.BigDecimal;

public class RowCountFunction implements Function {
  public RowCountFunction() {
  }

  public String getCanonicalName() {
    return "ROWCOUNT";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount > 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    final int groupStart;
    if ( parameterCount == 0 ) {
      groupStart = rfc.getRuntime().getGroupStartRow( -1 );
    } else {
      final String groupName =
          context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
      groupStart = rfc.getRuntime().getGroupStartRow( groupName );
    }
    final int row = rfc.getRuntime().getCurrentRow();
    // noinspection UnpredictableBigDecimalConstructorCall
    return new TypeValuePair( NumberType.GENERIC_NUMBER, new BigDecimal( (double) ( row - groupStart ) ) );
  }
}
