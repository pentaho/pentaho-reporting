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
 * Creation-Date: 06.06.2007, 18:52:25
 *
 * @author Thomas Morgner
 */
public abstract class AbstractCompareOperator implements InfixOperator {
  private static final TypeValuePair RETURN_TRUE = new TypeValuePair( LogicalType.TYPE, Boolean.TRUE );
  private static final TypeValuePair RETURN_FALSE = new TypeValuePair( LogicalType.TYPE, Boolean.FALSE );
  private static final long serialVersionUID = 1375799912336916036L;

  protected AbstractCompareOperator() {
  }

  public final TypeValuePair evaluate( final FormulaContext context,
                                       final TypeValuePair value1,
                                       final TypeValuePair value2 )
    throws EvaluationException {
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Type type1 = value1.getType();
    final Type type2 = value2.getType();
    final Object value1Raw = value1.getValue();
    final Object value2Raw = value2.getValue();
    if ( value1Raw == null || value2Raw == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final ExtendedComparator comparator = typeRegistry.getComparator( type1, type2 );
    final int result = comparator.compare( type1, value1Raw, type2, value2Raw );
    if ( evaluate( result ) ) {
      return RETURN_TRUE;
    }
    return RETURN_FALSE;
  }

  protected abstract boolean evaluate( int compareResult );
}
