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
 * Divides all values read from the field-list. This is almost the same as the formula <code>[field1] / [field2] /
 * field[3] / .. / [fieldn]</code>. Values that are non-numeric or null are ignored.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class ColumnDivisionExpression extends ColumnAggregationExpression {
  /**
   * The scale-property defines the precission of the divide-operation.
   */
  private int scale;
  /**
   * The rounding-property defines the precission of the divide-operation.
   */
  private int roundingMode;

  /**
   * Default constructor.
   */
  public ColumnDivisionExpression() {
    scale = 14;
    roundingMode = BigDecimal.ROUND_HALF_UP;
  }

  /**
   * Returns the defined rounding mode. This influences the precision of the divide-operation.
   *
   * @return the rounding mode.
   * @see java.math.BigDecimal#divide(java.math.BigDecimal, int)
   */
  public int getRoundingMode() {
    return roundingMode;
  }

  /**
   * Defines the rounding mode. This influences the precision of the divide-operation.
   *
   * @param roundingMode
   *          the rounding mode.
   * @see java.math.BigDecimal#divide(java.math.BigDecimal, int)
   */
  public void setRoundingMode( final int roundingMode ) {
    this.roundingMode = roundingMode;
  }

  /**
   * Returns the scale for the divide-operation. The scale influences the precision of the division.
   *
   * @return the scale.
   */
  public int getScale() {
    return scale;
  }

  /**
   * Defines the scale for the divide-operation. The scale influences the precision of the division.
   *
   * @param scale
   *          the scale.
   */
  public void setScale( final int scale ) {
    this.scale = scale;
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
        if ( n.doubleValue() == 0 ) {
          // No matter what goes in next, we will always result in zero.
          return n;
        }
      } else {
        if ( n.doubleValue() == 0 ) {
          return null;
        }
        computedResult = computedResult.divide( nval, scale, roundingMode );
      }
    }

    return computedResult;
  }
}
