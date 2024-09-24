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

/**
 * Creation-Date: 31.10.2006, 16:34:11
 *
 * @author Thomas Morgner
 */
public class LesserOperator extends AbstractCompareOperator {
  private static final long serialVersionUID = 4305611883854102139L;

  public LesserOperator() {
  }

  protected boolean evaluate( final int compareResult ) {
    return compareResult < 0;
  }

  public int getLevel() {
    return 400;
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
    return false;
  }

  /**
   * returns the string representation of this operator.
   *
   * @return string
   */
  public String toString() {
    return "<";
  }
}
