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

package org.pentaho.reporting.engine.classic.core.function.bool;

import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;

/**
 * Computes the logical OR of all fields given. Non-boolean values are ignored and have no influence on the result.
 *
 * @author Thomas Morgner
 * @deprecated use Formulas instead
 */
public class OrExpression extends ColumnAggregationExpression {
  /**
   * Default Constructor.
   */
  public OrExpression() {
  }

  /**
   * Computes the logical OR of all fields given. Non-boolean values are ignored and have no influence on the result.
   *
   * @return Boolean.TRUE or Boolean.FALSE
   */
  public Object getValue() {
    final Object[] values = getFieldValues();
    final int length = values.length;
    if ( length == 0 ) {
      return Boolean.FALSE;
    }

    for ( int i = 0; i < length; i++ ) {
      final Object value = values[i];
      if ( value instanceof Boolean == false ) {
        continue;
      }

      final Boolean n = (Boolean) value;
      if ( n.equals( Boolean.TRUE ) ) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }
}
