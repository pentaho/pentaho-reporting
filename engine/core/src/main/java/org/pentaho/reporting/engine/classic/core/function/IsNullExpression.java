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


package org.pentaho.reporting.engine.classic.core.function;

/**
 * Checks whether a field contains a NULL value.
 *
 * @author Thomas Morgner
 * @deprecated Use a Formula Instead
 */
public class IsNullExpression extends AbstractExpression {
  /**
   * The field name.
   */
  private String field;

  /**
   * Default constructor.
   */
  public IsNullExpression() {
  }

  /**
   * Returns the name of the field from where to read the value.
   *
   * @return the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the field from where to read the value.
   *
   * @param field
   *          the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object o = getDataRow().get( getField() );
    if ( o == null ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
