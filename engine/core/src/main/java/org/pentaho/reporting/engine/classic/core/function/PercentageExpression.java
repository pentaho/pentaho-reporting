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
 * Computes the percentage for a column in relation to a base column.
 * <p/>
 * The function undestands two parameters. The <code>dividend</code> parameter is required and denotes the name of an
 * ItemBand-field which is used as dividend. The <code>divisor</code> parameter is required and denotes the name of an
 * ItemBand-field which is uses as divisor.
 * <p/>
 * If either the divident or the divisor are not numeric, the expression will return <code>null</code>.
 * <p/>
 * The formula used is as follows:
 * 
 * <pre>
 * Percent := divident / divisor
 * </pre>
 * <p/>
 * If the flag <code>useDifference</code> is set, the difference between base and subject is used instead.
 * 
 * <pre>
 * Percent := (divisor - divident) / divisor
 * </pre>
 *
 * @author Heiko Evermann
 * @author Thomas Morgner
 * @deprecated The same can be achieved using a simple ValueExpression.
 */
@SuppressWarnings( "deprecation" )
public class PercentageExpression extends AbstractExpression {
  /**
   * A constant for the value ZERO.
   */
  private static final BigDecimal ZERO = new BigDecimal( 0 );
  /**
   * the field used as dividend by the function.
   */
  private String dividend;
  /**
   * the field used as divisor by the function.
   */
  private String divisor;
  /**
   * A flag indicating whether the difference between divident and divisor should be used as real divisor.
   */
  private boolean useDifference;
  /**
   * The scale-property defines the precission of the divide-operation.
   */
  private int scale;
  /**
   * The rounding-property defines the precission of the divide-operation.
   */
  private int roundingMode;

  /**
   * Constructs a new function.
   * <P>
   * Initially the function has no name...be sure to assign one before using the function.
   */
  public PercentageExpression() {
    scale = 14;
    roundingMode = BigDecimal.ROUND_HALF_UP;
  }

  /**
   * Returns whether the difference between divident and divisor should be used as real divisor.
   *
   * @return true, if the difference is used, false if the divident is used directly.
   */
  public boolean isUseDifference() {
    return useDifference;
  }

  /**
   * Defines whether the difference between divident and divisor should be used as real divisor.
   *
   * @param useDifference
   *          true, if the difference is used, false if the divident is used directly.
   */
  public void setUseDifference( final boolean useDifference ) {
    this.useDifference = useDifference;
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
   * Returns the field used as dividend by the function.
   * <P>
   * The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getDividend() {
    return dividend;
  }

  /**
   * Returns the field used as divisor by the function.
   * <P>
   * The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getDivisor() {
    return divisor;
  }

  /**
   * Sets the field name to be used as dividend for the function.
   * <P>
   * The field name corresponds to a column name in the report's data-row.
   *
   * @param dividend
   *          the field name.
   */
  public void setDividend( final String dividend ) {
    this.dividend = dividend;
  }

  /**
   * Sets the field name to be used as divisor for the function.
   * <P>
   * The field name corresponds to a column name in the report's data-row.
   *
   * @param divisor
   *          the field name.
   */
  public void setDivisor( final String divisor ) {
    this.divisor = divisor;
  }

  /**
   * Return the current function value.
   * <P>
   * The value is calculated as the quotient of two columns: the dividend column and the divisor column. If the divisor
   * is zero, the return value is "n/a";
   *
   * @return The quotient
   */
  public Object getValue() {
    if ( dividend == null || divisor == null ) {
      return null;
    }

    final Object dividentFieldValue = getDataRow().get( getDividend() );
    // do not add when field is null or no number
    final BigDecimal dividend;
    if ( dividentFieldValue instanceof BigDecimal ) {
      dividend = (BigDecimal) dividentFieldValue;
    } else if ( dividentFieldValue instanceof Number == false ) {
      return null;
    } else {
      dividend = new BigDecimal( dividentFieldValue.toString() );
    }

    final Object divisorFieldValue = getDataRow().get( getDivisor() );
    // do not add when field is null or no number
    final BigDecimal divisor;
    if ( divisorFieldValue instanceof BigDecimal ) {
      divisor = (BigDecimal) divisorFieldValue;
    } else if ( divisorFieldValue instanceof Number == false ) {
      return null;
    } else {
      divisor = new BigDecimal( divisorFieldValue.toString() );
    }

    if ( PercentageExpression.ZERO.compareTo( divisor ) == 0 ) {
      return null;
    }

    if ( useDifference ) {
      final BigDecimal delta = dividend.subtract( divisor );
      return delta.divide( divisor, scale, roundingMode );
    } else {
      return dividend.divide( divisor, scale, roundingMode );
    }
  }
}
