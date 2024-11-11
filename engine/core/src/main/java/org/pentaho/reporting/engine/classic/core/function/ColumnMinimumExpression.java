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

/**
 * Computes the minimum of all data-row columns defined in the field-list. This computes the horizontal minimum, to
 * compute the minimum value in a group, use the ItemMinFunction instead.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class ColumnMinimumExpression extends ColumnAggregationExpression {
  /**
   * Default Constructor.
   */
  public ColumnMinimumExpression() {
  }

  /**
   * Returns the minimum value. Non-comparable values are ignored.
   *
   * @return the minimum value computed by the function.
   */
  public Object getValue() {
    final Object[] values = getFieldValues();
    Comparable computedResult = null;
    for ( int i = 0; i < values.length; i++ ) {
      final Object value = values[i];
      if ( value instanceof Comparable == false ) {
        continue;
      }

      final Comparable n = (Comparable) value;
      if ( computedResult == null ) {
        computedResult = n;
      } else if ( computedResult.compareTo( n ) < 0 ) {
        computedResult = n;
      }
    }
    return computedResult;
  }
}
