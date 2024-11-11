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
