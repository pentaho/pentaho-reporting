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
