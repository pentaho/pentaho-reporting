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
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

/**
 * Creation-Date: 08.10.2006, 11:34:40
 *
 * @author Thomas Morgner
 */
public class StaticValue extends AbstractLValue {
  private Object value;
  private Type type;
  private static final long serialVersionUID = 7255803922294601237L;

  public StaticValue( final Object value ) {
    this( value, AnyType.TYPE );
  }

  public StaticValue( final Object value, final Type type ) {
    this.value = value;
    this.type = type;
  }

  public StaticValue( final Object value, final ParsePosition parsePosition ) {
    this( value, AnyType.TYPE, parsePosition );
  }

  public StaticValue( final Object value, final Type type, final ParsePosition parsePosition ) {
    this.value = value;
    this.type = type;
    setParsePosition( parsePosition );
  }

  public void initialize( final FormulaContext context ) throws EvaluationException {
  }

  public TypeValuePair evaluate() {
    return new TypeValuePair( type, value );
  }


  public String toString() {
    if ( value instanceof Number ) {
      return String.valueOf( value );
    }

    return FormulaUtil.quoteString( String.valueOf( value ) );
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant() {
    return true;
  }

  public Object getValue() {
    return value;
  }

  /**
   * This function allows a program traversing the LibFormula object model to know what type this static value is.
   *
   * @return the type of the static value
   */
  public Type getValueType() {
    return type;
  }
}
