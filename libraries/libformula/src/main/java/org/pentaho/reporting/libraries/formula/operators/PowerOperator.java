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

/**
 * This has to be implemented manually if we want to support arbitary precision. Damn, do I have to implement the
 * logarithm computation as well? For now: Ignore that and use doubles!
 *
 * @author Thomas Morgner
 */
public class PowerOperator extends AbstractNumericOperator {
  private static final long serialVersionUID = -2788666171805222287L;

  public PowerOperator() {
  }

  protected Number evaluate( final Number number1, final Number number2 ) throws EvaluationException {
    final double result = StrictMath.pow( number1.doubleValue(), number2.doubleValue() );
    return new Double( result );
  }

  public int getLevel() {
    return 0;
  }

  public String toString() {
    return "^";
  }

  public boolean isLeftOperation() {
    return false;
  }

  /**
   * Defines, whether the operation is associative. For associative operations, the evaluation order does not matter, if
   * the operation appears more than once in an expression, and therefore we can optimize them a lot better than
   * non-associative operations (ie. merge constant parts and precompute them once).
   *
   * @return true, if the operation is associative, false otherwise
   */
  public boolean isAssociative() {
    return false;
  }

}
