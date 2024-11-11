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


package org.pentaho.reporting.engine.classic.core.function.numeric;

import org.pentaho.reporting.engine.classic.core.function.AbstractCompareExpression;

/**
 * A function that compares a static number with a number read from a field.
 *
 * @author Thomas Morgner
 * @deprecated like all compare functions, using the formula support is easier.
 */
@SuppressWarnings( "deprecation" )
public class CompareNumberExpression extends AbstractCompareExpression {
  /**
   * The number to which the field's value get compared.
   */
  private Double number;

  /**
   * Default constructor.
   */
  public CompareNumberExpression() {
    number = new Double( 0 );
  }

  /**
   * Returns the static value to which the field's value is compared.
   *
   * @return the static value.
   */
  protected Comparable getComparable() {
    return number;
  }

  /**
   * Returns the static number value.
   *
   * @return the static number.
   */
  public double getNumber() {
    return number.doubleValue();
  }

  /**
   * Sets the static number value.
   *
   * @param number
   *          the static number.
   */
  public void setNumber( final double number ) {
    this.number = new Double( number );
  }
}
