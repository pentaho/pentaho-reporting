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
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.ErrorType;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

/**
 * A reference that queries the datarow.
 *
 * @author Thomas Morgner
 */
public class ContextLookup extends AbstractLValue {
  private String name;
  private static final long serialVersionUID = 2882834743999159722L;

  public ContextLookup( final String name ) {
    this( name, null );
  }

  public ContextLookup( final String name, final ParsePosition parsePosition ) {
    this.name = name;
    setParsePosition( parsePosition );
  }

  public TypeValuePair evaluate() throws EvaluationException {
    final FormulaContext context = getContext();
    final Type type = context.resolveReferenceType( name );
    final Object value = context.resolveReference( name );
    if ( value == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }
    return new TypeValuePair( type, value );
  }

  public Type getValueType() {
    try {
      final FormulaContext context = getContext();
      return context.resolveReferenceType( name );
    } catch ( final EvaluationException evalex ) {
      // exception ignored.
      return ErrorType.TYPE;
    }
  }

  public String toString() {
    return FormulaUtil.quoteReference( name );
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant() {
    return false;
  }

  public String getName() {
    return name;
  }
}
