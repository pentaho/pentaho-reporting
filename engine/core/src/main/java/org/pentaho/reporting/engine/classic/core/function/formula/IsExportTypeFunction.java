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
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Tests, whether a certain export type is currently used. This matches the given export type with the export type that
 * is specified by the output-target. The given export type can be a partial pattern, in which case this expression
 * tests, whether the given export type is a sub-type of the output-target's type.
 * <p/>
 * To test whether a table-export is used, specifiy the export type as "table" and it will match all table exports.
 *
 * @author Thomas Morgner
 */
public class IsExportTypeFunction implements Function {
  /**
   * Default Constructor. Does nothing.
   */
  public IsExportTypeFunction() {
  }

  /**
   * Returns the Canonical Name of the function.
   *
   * @return the constant string "ISEXPORTTYPE"
   */
  public String getCanonicalName() {
    return "ISEXPORTTYPE";
  }

  /**
   * Return Boolean.TRUE, if the specified export type matches the used export type, Boolean.FALSE otherwise.
   *
   * @param context
   *          the formula context, which allows access to the runtime.
   * @param parameters
   *          the parameter callback is used to retrieve parameter values.
   * @return the computed result wrapped in a TypeValuePair.
   * @throws EvaluationException
   *           if an error occurs.
   */
  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    if ( context instanceof ReportFormulaContext == false ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final Object value = parameters.getValue( 0 );
    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    if ( value != null && rfc.getExportType().startsWith( String.valueOf( value ) ) ) {
      return new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
    }

    return new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  }
}
