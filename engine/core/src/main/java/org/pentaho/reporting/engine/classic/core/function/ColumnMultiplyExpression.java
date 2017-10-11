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
