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


package org.pentaho.jfreereport.legacy;


import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;

import java.math.BigDecimal;

/**
 * @deprecated These functions are no longer supported.
 */
public class ColumnDifferenceExpression extends ColumnAggregationExpression {

  public ColumnDifferenceExpression() {
  }


  /**
   * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    Object[] values = getFieldValues();
    BigDecimal computedResult = null;
    for ( int i = 0; i < values.length; i++ ) {
      Object value = values[ i ];
      if ( value instanceof Number ) {
        Number n = (Number) value;
        if ( computedResult == null ) {
          //noinspection ObjectToString
          computedResult = new BigDecimal( n.toString() );
        } else {
          //noinspection ObjectToString
          computedResult = computedResult.subtract( new BigDecimal( n.toString() ) );
        }
      }
    }

    if ( computedResult != null ) {
      return computedResult.stripTrailingZeros();
    } else {
      return null;
    }
  }
}
