/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
