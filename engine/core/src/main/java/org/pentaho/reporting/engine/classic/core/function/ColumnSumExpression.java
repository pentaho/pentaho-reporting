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


package org.pentaho.reporting.engine.classic.core.function;

import java.math.BigDecimal;

/**
 * Adds all values read from the field-list. This is almost the same as the formula <code>[field1] + [field2] + field[3]
 * + .. + [fieldn]</code>. Values that are non-numeric or null are ignored.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class ColumnSumExpression extends ColumnAggregationExpression {
  /**
   * Default Constructor.
   */
  public ColumnSumExpression() {
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object[] values = getFieldValues();
    BigDecimal computedResult = new BigDecimal( 0 );
    for ( int i = 0; i < values.length; i++ ) {
      final Object value = values[i];
      if ( value instanceof Number == false ) {
        continue;
      }

      final Number n = (Number) value;
      final BigDecimal nval = new BigDecimal( n.toString() );
      computedResult = computedResult.add( nval );
    }

    return computedResult;
  }
}
