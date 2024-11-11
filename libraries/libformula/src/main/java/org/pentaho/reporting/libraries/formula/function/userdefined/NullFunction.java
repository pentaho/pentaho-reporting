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


package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

/**
 * Creation-Date: Dec 18, 2006, 12:54:55 PM
 *
 * @author Thomas Morgner
 * @deprecated
 */
public class NullFunction implements Function {
  private static final TypeValuePair NULL = new TypeValuePair( AnyType.TYPE, null );
  private static final long serialVersionUID = -2213352007005452516L;

  public NullFunction() {
  }

  public String getCanonicalName() {
    return "NULL";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    throw new EvaluationException( LibFormulaErrorValue.ERROR_NA_VALUE );
  }

}
