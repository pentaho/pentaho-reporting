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


package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

import java.io.Serializable;

/**
 * An operator. An operator always takes two arguments. Prefix and postfix operators are implemented differently.
 *
 * @author Thomas Morgner
 */
public interface InfixOperator extends Serializable {
  /**
   * Evaluates the comptuation for both parameters. This method must never return null.
   *
   * @param context
   * @param value1
   * @param value2
   * @return
   * @throws EvaluationException
   */
  public TypeValuePair evaluate( FormulaContext context,
                                 TypeValuePair value1, TypeValuePair value2 )
    throws EvaluationException;

  public int getLevel();

  /**
   * Defines the bind-direction of the operator. That direction defines, in which direction a sequence of equal
   * operators is resolved.
   *
   * @return true, if the operation is left-binding, false if right-binding
   */
  public boolean isLeftOperation();

  /**
   * Defines, whether the operation is associative. For associative operations, the evaluation order does not matter, if
   * the operation appears more than once in an expression, and therefore we can optimize them a lot better than
   * non-associative operations (ie. merge constant parts and precompute them once).
   *
   * @return true, if the operation is associative, false otherwise
   */
  public boolean isAssociative();
}
