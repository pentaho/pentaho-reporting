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

import java.math.BigDecimal;

/**
 * A sequence that treats text-values as valid numbers that always evaluate to zero. Logical values are treated as
 * numbers as well and always evaluate to 0 for false and 1 for true.
 *
 * @author Cedric Pronzato
 */
public class AnyNumberSequence extends AnySequence implements NumberSequence {
  private static final Number ZERO = new BigDecimal( "0" );

  /**
   * Empty number sequence.
   */
  public AnyNumberSequence( final FormulaContext context ) {
    super( context );
  }

  /**
   * Number sequence bounded to only one number item.
   *
   * @param n A number
   */
  public AnyNumberSequence( final LValue n, final FormulaContext context ) {
    super( n, context );
  }

  /**
   * Number sequence bounded to an array.
   *
   * @param array
   */
  public AnyNumberSequence( final ArrayCallback array, final FormulaContext context ) {
    super( array, context );
  }

  public AnyNumberSequence( final AnySequence anySequence ) {
    super( anySequence );
  }

  protected boolean isValidNext( final LValue o ) throws EvaluationException {
    if ( o == null ) {
      return false;
    }
    final Type type = o.getValueType();
    if ( type.isFlagSet( Type.NUMERIC_TYPE ) ) {
      return true;
    }
    if ( type.isFlagSet( Type.LOGICAL_TYPE ) ) {
      return true;
    }
    if ( type.isFlagSet( Type.TEXT_TYPE ) ) {
      return true;
    }
    if ( type.isFlagSet( Type.ANY_TYPE ) ) {
      return true;
    }
    return false;
  }

  public Number nextNumber() throws EvaluationException {
    final Object value = super.next();
    if ( value instanceof Number ) {
      return (Number) value;
    }

    return ZERO;
  }
}
