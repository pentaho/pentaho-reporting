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

package org.pentaho.reporting.engine.classic.core.function.numeric;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * Checks whether the column specified by the field name contains a positive number.
 *
 * @author Thomas Morgner
 * @deprecated This can be replaced by a formula.
 */
public class IsPositiveExpression extends AbstractExpression {
  /**
   * The field name.
   */
  private String field;

  /**
   * Default constructor.
   */
  public IsPositiveExpression() {
  }

  /**
   * Checks whether the column specified by the field name contains a positive number. Non-numeric values are not
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
    if ( n.doubleValue() >= 0 ) {
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
