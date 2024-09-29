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
