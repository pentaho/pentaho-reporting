/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.jfreereport.legacy;

import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;

import java.math.BigDecimal;

/**
 * @deprecated These functions are no longer supported.
 */
public class ColumnDivisionExpression extends ColumnAggregationExpression {
  private static final int DEFAULT_SCALE = 14;


  public ColumnDivisionExpression() {
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
          computedResult =
            computedResult.divide( new BigDecimal( n.toString() ), DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP );
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
