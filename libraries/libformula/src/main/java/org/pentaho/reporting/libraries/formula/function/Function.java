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


package org.pentaho.reporting.libraries.formula.function;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

import java.io.Serializable;

/**
 * A function is an arbitary computation. A return value type is not available unless the function has been evaluated.
 * <p/>
 * Functions must be stateless, that means: Calling the same function with exactly the same parameters must always
 * result in the same computed value.
 *
 * @author Thomas Morgner
 */
public interface Function extends Serializable {
  public String getCanonicalName();

  public TypeValuePair evaluate( FormulaContext context,
                                 ParameterCallback parameters )
    throws EvaluationException;
}
