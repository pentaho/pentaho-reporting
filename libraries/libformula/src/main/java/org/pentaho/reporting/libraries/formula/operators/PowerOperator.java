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
