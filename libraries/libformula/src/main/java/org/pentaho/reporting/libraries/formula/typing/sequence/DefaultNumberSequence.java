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


package org.pentaho.reporting.libraries.formula.typing.sequence;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.NumberSequence;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * @author Cedric Pronzato
 */
public class DefaultNumberSequence extends AnySequence implements NumberSequence {
  /**
   * Empty number sequence.
   */
  public DefaultNumberSequence( final FormulaContext context ) {
    super( context );
  }

  /**
   * Number sequence bounded to only one number item.
   *
   * @param n A number
   */
  public DefaultNumberSequence( final LValue n, final FormulaContext context ) {
    super( n, context );
  }

  /**
   * Number sequence bounded to an array.
   *
   * @param array
   */
  public DefaultNumberSequence( final ArrayCallback array, final FormulaContext context ) {
    super( array, context );
  }

  public DefaultNumberSequence( final AnySequence anySequence ) {
    super( anySequence );
  }

  protected boolean isValidNext( final LValue o ) throws EvaluationException {
    if ( o == null ) {
      return false;
    }
    final Type type = o.getValueType();
    if ( type == null ) {
      throw new IllegalStateException();
    }
    if ( type.isFlagSet( Type.NUMERIC_TYPE ) ) {
      return true;
    }
    return false;
  }

  public Number nextNumber() throws EvaluationException {
    final Object value = super.next();
    if ( value instanceof Number ) {
      return (Number) value;
    }

    return null;
  }
}
