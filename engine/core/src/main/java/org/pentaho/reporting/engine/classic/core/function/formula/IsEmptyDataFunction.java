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
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

public class IsEmptyDataFunction implements Function {
  public IsEmptyDataFunction() {
  }

  public String getCanonicalName() {
    return "ISEMPTYDATA";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    final boolean value = rfc.isResultSetEmpty();

    if ( value ) {
      return new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
    }

    return new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  }
}
