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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
