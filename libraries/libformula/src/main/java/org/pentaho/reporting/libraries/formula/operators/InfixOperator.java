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
