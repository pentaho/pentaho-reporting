/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import java.math.BigDecimal;

/**
 * Computes the horizontal average over all columns specified in the field-list. The average will be computed over all
 * fields of the current data-row, it will not be computed for all rows in the group. For that use the
 * {@link org.pentaho.reporting.engine.classic.core.function.ItemAvgFunction} instead.
 * <p/>
 * Non numeric and null-columns will be treated as zero for the task of summing up all members. Whether these fields are
 * used counted as valid fields can be controlled with the 'onlyValidFields' flag.
 *
 * @author Thomas Morgner
 * @deprecated the same can be achived with a formula "AVG([column1], [column2], [column3])"
 */
public class ColumnAverageExpression extends ColumnAggregationExpression {
  /**
   * A flag defining whether non-numeric and null-values should be ignored.
   */
  private boolean onlyValidFields;

  /**
   * A flag defining whether the expression should return infinity if there are no valid fields. If set to false, this
   * expression returns null instead.
   */
  private boolean returnInfinity;
  /**
   * The scale-property defines the precission of the divide-operation.
   */
  private int scale;
  /**
   * The rounding-property defines the precission of the divide-operation.
   */
  private int roundingMode;

  /**
   * Default Constructor.
   */
  public ColumnAverageExpression() {
    this.returnInfinity = true; // for backward compatiblity.
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
   * Returns, whether the expression returns infinity if there are no valid fields. If set to false, this expression
   * returns null instead.
   *
   * @return true, if infinity is returned, false otherwise.
   */
  public boolean isReturnInfinity() {
    return returnInfinity;
  }

  /**
   * Defines, whether the expression returns infinity if there are no valid fields. If set to false, this expression
   * returns null instead.
   *
   * @param returnInfinity
   *          true, if infinity is returned, false otherwise.
   */
  public void setReturnInfinity( final boolean returnInfinity ) {
    this.returnInfinity = returnInfinity;
  }

  /**
   * Returns, whether non-numeric and null-values are ignored for the average-computation.
   *
   * @return true, if the invalid fields will be ignored, false if they count as valid zero-value fields.
   */
  public boolean isOnlyValidFields() {
    return onlyValidFields;
  }

  /**
   * Defines, whether non-numeric and null-values are ignored for the average-computation.
   *
   * @param onlyValidFields
   *          true, if the invalid fields will be ignored, false if they count as valid zero-value fields.
   */
  public void setOnlyValidFields( final boolean onlyValidFields ) {
    this.onlyValidFields = onlyValidFields;
  }

  /**
   * Computes the horizontal average of all field in the field-list. The average will be computed over all fields of the
   * current data-row, it will not be computed for all rows in the group. For that use the
   * {@link org.pentaho.reporting.engine.classic.core.function.ItemAvgFunction} instead.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object[] values = getFieldValues();
    BigDecimal computedResult = new BigDecimal( 0 );
    int count = 0;
    for ( int i = 0; i < values.length; i++ ) {
      final Object value = values[i];
      if ( value instanceof Number == false ) {
        continue;
      }

      final Number n = (Number) value;
      final BigDecimal nval = new BigDecimal( n.toString() );
      computedResult = computedResult.add( nval );
      count += 1;
    }

    if ( onlyValidFields ) {
      if ( count == 0 ) {
        if ( returnInfinity == false ) {
          return null;
        }
        if ( computedResult.signum() == -1 ) {
          return new Double( Double.NEGATIVE_INFINITY );
        } else {
          return new Double( Double.POSITIVE_INFINITY );
        }
      }
      return computedResult.divide( new BigDecimal( count ), scale, roundingMode );
    }

    if ( values.length == 0 ) {
      if ( returnInfinity == false ) {
        return null;
      }
      if ( computedResult.signum() == -1 ) {
        return new Double( Double.NEGATIVE_INFINITY );
      } else {
        return new Double( Double.POSITIVE_INFINITY );
      }
    }
    return computedResult.divide( new BigDecimal( values.length ), scale, roundingMode );
  }
}
