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

package org.pentaho.reporting.libraries.formula.lvalues;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.typing.Type;

import java.io.Serializable;

/**
 * A reference is an indirection to hide the details of where the actual value came from.
 * <p/>
 * The reference is responsible to report dependencies.
 *
 * @author Thomas Morgner
 */
public interface LValue extends Serializable, Cloneable {
  public void initialize( FormulaContext context ) throws EvaluationException;

  public TypeValuePair evaluate() throws EvaluationException;

  public Object clone() throws CloneNotSupportedException;

  /**
   * Querying the value type is only valid *after* the value has been evaluated.
   *
   * @return
   */
  public Type getValueType();

  /**
   * Returns any dependent lvalues (parameters and operands, mostly).
   *
   * @return
   */
  public LValue[] getChildValues();

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant();

  public ParsePosition getParsePosition();
}
