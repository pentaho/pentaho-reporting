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

/**
 * Computes the maximum of all data-row columns defined in the field-list. This computes the horizontal maximum, to
 * compute the maximum value in a group, use the ItemMaxFunction instead.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class ColumnMaximumExpression extends ColumnAggregationExpression {
  /**
   * Default Constructor.
   */
  public ColumnMaximumExpression() {
  }

  /**
   * Returns the maximum value. Non-comparable values are ignored.
   *
   * @return the maximum value computed by the function.
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
      } else if ( computedResult.compareTo( n ) > 0 ) {
        computedResult = n;
      }
    }
    return computedResult;
  }
}
