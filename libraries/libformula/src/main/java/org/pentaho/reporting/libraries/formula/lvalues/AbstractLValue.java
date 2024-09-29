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
