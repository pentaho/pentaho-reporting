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
