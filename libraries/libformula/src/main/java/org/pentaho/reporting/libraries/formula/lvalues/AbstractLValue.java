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

/**
 * Creation-Date: 01.11.2006, 18:19:00
 *
 * @author Thomas Morgner
 */
public abstract class AbstractLValue implements LValue {
  private static final LValue[] EMPTY_CHILDS = new LValue[ 0 ];

  private transient FormulaContext context;
  private static final long serialVersionUID = -8929559303303911502L;
  private ParsePosition parsePosition;

  protected AbstractLValue() {
  }

  public ParsePosition getParsePosition() {
    return parsePosition;
  }

  public void setParsePosition( final ParsePosition parsePosition ) {
    this.parsePosition = parsePosition;
  }

  public void initialize( final FormulaContext context ) throws EvaluationException {
    this.context = context;
  }

  public FormulaContext getContext() {
    if ( context == null ) {
      throw new NullPointerException();
    }
    return context;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Returns any dependent lvalues (parameters and operands, mostly).
   *
   * @return
   */
  public LValue[] getChildValues() {
    return EMPTY_CHILDS;
  }

  /**
   * Querying the value type is only valid *after* the value has been evaluated.
   *
   * @return
   */
  public Type getValueType() {
    return null;
  }
}
