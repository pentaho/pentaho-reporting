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


package org.pentaho.reporting.engine.classic.core.function;

import java.math.BigDecimal;

/**
 * Multiplies all values read from the field-list. This is almost the same as the formula <code>[field1] * [field2] *
 * field[3] * .. * [fieldn]</code>. Values that are non-numeric or null are ignored.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class ColumnMultiplyExpression extends ColumnAggregationExpression {
  /**
   * Default Constructor.
   */
  public ColumnMultiplyExpression() {
  }

  /**
   * Return the current expression value.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object[] values = getFieldValues();
    BigDecimal computedResult = null;

    for ( int i = 0; i < values.length; i++ ) {
      final Object value = values[i];
      if ( value instanceof Number == false ) {
        continue;
      }

      final Number n = (Number) value;
      final BigDecimal nval = new BigDecimal( n.toString() );
      if ( computedResult == null ) {
        computedResult = nval;
      } else {
        computedResult = computedResult.multiply( nval );
      }
    }

    return computedResult;
  }
}
