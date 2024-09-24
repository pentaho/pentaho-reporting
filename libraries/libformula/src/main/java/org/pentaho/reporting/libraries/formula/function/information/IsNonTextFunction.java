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

package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * This function retruns false if the given value is of type Text.
 *
 * @author Cedric Pronzato
 */
public class IsNonTextFunction extends IsTextFunction {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = -372702612173903875L;

  public IsNonTextFunction() {
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final TypeValuePair typeValuePair = super.evaluate( context, parameters );
    if ( typeValuePair.getValue().equals( Boolean.TRUE ) ) {
      return RETURN_FALSE;
    } else {
      return RETURN_TRUE;
    }
  }

  public String getCanonicalName() {
    return "ISNONTEXT";
  }

}
