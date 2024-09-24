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
import org.pentaho.reporting.libraries.formula.operators.PostfixOperator;

/**
 * Creation-Date: 02.11.2006, 10:20:27
 *
 * @author Thomas Morgner
 */
public class PostfixTerm extends AbstractLValue {
  private PostfixOperator operator;
  private LValue value;
  private static final long serialVersionUID = -3652663226326546952L;

  public PostfixTerm( final LValue value, final PostfixOperator operator ) {
    if ( operator == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.operator = operator;
    this.value = value;
  }

  public PostfixOperator getOperator() {
    return operator;
  }

  public LValue getValue() {
    return value;
  }

  public TypeValuePair evaluate() throws EvaluationException {
    return operator.evaluate( getContext(), value.evaluate() );
  }


  public String toString() {
    return String.valueOf( value ) + operator;
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant() {
    return value.isConstant();
  }

  /**
   * Returns any dependent lvalues (parameters and operands, mostly).
   *
   * @return
   */
  public LValue[] getChildValues() {
    return new LValue[] { value };
  }

  public Object clone() throws CloneNotSupportedException {
    final PostfixTerm o = (PostfixTerm) super.clone();
    o.value = (LValue) value.clone();
    return o;
  }
}
