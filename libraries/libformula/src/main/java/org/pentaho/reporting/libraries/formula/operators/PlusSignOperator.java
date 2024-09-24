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

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

/**
 * This is a no-op-operator which is equal to "zero plus x".
 *
 * @author Thomas Morgner
 */
public class PlusSignOperator implements PrefixOperator {
  private static final long serialVersionUID = 8127033177252320339L;

  public PlusSignOperator() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final TypeValuePair value1 ) throws EvaluationException {
    if ( value1 == null ) {
      // This is fatal, but should never happen.
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
    return value1;
  }

  public String toString() {
    return "+";
  }

}
