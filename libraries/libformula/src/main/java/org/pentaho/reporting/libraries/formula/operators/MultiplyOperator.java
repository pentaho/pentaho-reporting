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
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Creation-Date: 31.10.2006, 16:34:11
 *
 * @author Thomas Morgner
 */
public class MultiplyOperator extends AbstractNumericOperator {
  private static final long serialVersionUID = 4121666193537297373L;

  public MultiplyOperator() {
  }

  protected Number evaluate( final Number number1, final Number number2 ) throws EvaluationException {
    if ( ( number1 instanceof Integer || number1 instanceof Short ) &&
      ( number2 instanceof Integer || number2 instanceof Short ) ) {
      // this is still safe ..
      return new BigDecimal( number1.longValue() * number2.longValue() );
    }

    final BigDecimal bd1 = NumberUtil.getAsBigDecimal( number1 );
    final BigDecimal bd2 = NumberUtil.getAsBigDecimal( number2 );
    return bd1.multiply( bd2 );
  }

  public int getLevel() {
    return 100;
  }

  public String toString() {
    return "*";
  }

  public boolean isLeftOperation() {
    return true;
  }

  /**
   * Defines, whether the operation is associative. For associative operations, the evaluation order does not matter, if
   * the operation appears more than once in an expression, and therefore we can optimize them a lot better than
   * non-associative operations (ie. merge constant parts and precompute them once).
   *
   * @return true, if the operation is associative, false otherwise
   */
  public boolean isAssociative() {
    return true;
  }

}
