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


package org.pentaho.reporting.engine.classic.core.function.numeric;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * Checks whether the column specified by the field name contains a negative number.
 *
 * @author Thomas Morgner
 * @deprecated This can be replaced by a formula.
 */
public class IsNegativeExpression extends AbstractExpression {
  /**
   * The field name.
   */
  private String field;

  /**
   * Default constructor.
   */
  public IsNegativeExpression() {
  }

  /**
   * Checks whether the column specified by the field name contains a negative number. Non-numeric values are not
   * compared and result in Boolean.FALSE.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object o = getDataRow().get( getField() );
    if ( o instanceof Number == false ) {
      return Boolean.FALSE;
    }
    final Number n = (Number) o;
    if ( n.doubleValue() < 0 ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  /**
   * Returns the name of the field from where to read the number.
   *
   * @return the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the field from where to read the number.
   *
   * @param field
   *          the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }
}
