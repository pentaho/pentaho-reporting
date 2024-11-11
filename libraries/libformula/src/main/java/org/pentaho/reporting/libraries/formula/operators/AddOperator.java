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

import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Null-Values are converted into ZERO
 *
 * @author Thomas Morgner
 */
public class AddOperator extends AbstractNumericOperator {
  private static final long serialVersionUID = -6676636760348655167L;

  public AddOperator() {
  }

  public Number evaluate( final Number number1, final Number number2 ) {
    if ( ( number1 instanceof Integer || number1 instanceof Short ) &&
      ( number2 instanceof Integer || number2 instanceof Short ) ) {
      return new BigDecimal( number1.longValue() + number2.longValue() );
    }

    final BigDecimal bd1 = NumberUtil.getAsBigDecimal( number1 );
    final BigDecimal bd2 = NumberUtil.getAsBigDecimal( number2 );
    return bd1.add( bd2 );
  }

  public int getLevel() {
    return 200;
  }


  public String toString() {
    return "+";
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
