/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
