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

package org.pentaho.jfreereport.legacy;

import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;

import java.math.BigDecimal;

/**
 * @deprecated These functions are no longer supported.
 */
public class ColumnAverageExpression extends ColumnAggregationExpression {
  private static final int DEFAULT_SCALE = 14;

  private boolean onlyValidFields;


  public ColumnAverageExpression() {
  }


  public boolean isOnlyValidFields() {
    return onlyValidFields;
  }


  public void setOnlyValidFields( final boolean onlyValidFields ) {
    this.onlyValidFields = onlyValidFields;
  }


  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    Object[] values = getFieldValues();
    BigDecimal computedResult = new BigDecimal( "0" );
    int count = 0;
    for ( int i = 0; i < values.length; i++ ) {
      Object value = values[ i ];
      if ( value instanceof Number ) {

        Number n = (Number) value;
        //noinspection ObjectToString
        computedResult = computedResult.add( new BigDecimal( n.toString() ) );
        count++;
      }
    }

    if ( onlyValidFields ) {
      return computedResult.divide( new BigDecimal( count ), DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP )
        .stripTrailingZeros();
    }

    if ( values.length > 0 ) {
      return computedResult.divide( new BigDecimal( values.length ), DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP )
        .stripTrailingZeros();
    } else {
      return BigDecimal.ZERO;
    }
  }
}
