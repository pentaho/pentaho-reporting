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


package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.ExtendedComparator;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;

/**
 * Creation-Date: 31.10.2006, 16:34:11
 *
 * @author Thomas Morgner
 */
public class EqualOperator implements InfixOperator {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 2865411431720931171L;

  public EqualOperator() {
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final TypeValuePair value1,
                                 final TypeValuePair value2 )
    throws EvaluationException {
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Object value1Raw = value1.getValue();
    final Object value2Raw = value2.getValue();
    if ( value1Raw == null || value2Raw == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final Type type1 = value1.getType();
    final Type type2 = value2.getType();
    final ExtendedComparator comparator = typeRegistry.getComparator( type1, type2 );
    final boolean result = comparator.isEqual( type1, value1Raw, type2, value2Raw );
    if ( result ) {
      return RETURN_TRUE;
    } else {
      return RETURN_FALSE;
    }
  }

  public int getLevel() {
    return 400;
  }


  public String toString() {
    return "=";
  }

  public boolean isLeftOperation() {
    return true;
  }

  /**
   * Defines, whether the operation is associative. For associative operations, the evaluation order does not matter, if
   * the operation appears more than once in an expression, and therefore we can optimize them a lot better than
   * non-associative operations (ie. merge constant parts and precompute them once).
   *
   * @return true, if the operation is associative, false otherwise
   */
  public boolean isAssociative() {
    return false;
  }

}
