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
 * Checks whether a field is empty. A field is considered empty, if it contains the value 'null', or an string that is
 * empty or only consists of whitespaces or a number that evaluates to zero.
 *
 * @author Thomas Morgner
 * @deprecated Use a Formula Instead
 */
public class IsEmptyExpression extends AbstractExpression {
  /**
   * The field name.
   */
  private String field;

  /**
   * Default constructor.
   */
  public IsEmptyExpression() {
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
    if ( o instanceof String ) {
      final String s = (String) o;
      if ( s.trim().length() == 0 ) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    if ( o instanceof Number ) {
      final Number n = (Number) o;
      if ( n.doubleValue() == 0 ) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
    return Boolean.FALSE;
  }
}
