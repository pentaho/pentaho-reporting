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


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function retruns true if the given value is of type Number.
 *
 * @author Cedric Pronzato
 */
public class IsNumberFunction implements Function {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 3298090211745552284L;

  public IsNumberFunction() {
  }

  public TypeValuePair evaluate(final FormulaContext context, final ParameterCallback parameters) throws EvaluationException
  {
    final int parameterCount = parameters.getParameterCount();
    if (parameterCount < 1)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE);
    }

    final Object value = parameters.getValue(0);
    if(value instanceof Number)
    {
      return RETURN_TRUE;
    }

    return RETURN_FALSE;
  }

  public String getCanonicalName() {
    return "ISNUMBER";
  }

}
