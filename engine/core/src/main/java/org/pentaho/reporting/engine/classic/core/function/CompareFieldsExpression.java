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
 * Compares the values of two fields.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula instead.
 */
@SuppressWarnings( "deprecation" )
public class CompareFieldsExpression extends AbstractCompareExpression {
  /**
   * The name of the data-row column that holds the second value.
   */
  private String otherField;

  /**
   * Default Constructor.
   */
  public CompareFieldsExpression() {
  }

  /**
   * Returns the name of the data-row column that holds the second value.
   *
   * @return the name of the other field.
   */
  public String getOtherField() {
    return otherField;
  }

  /**
   * Defines the name of the data-row column that holds the second value.
   *
   * @param otherField
   *          the name of the other field.
   */
  public void setOtherField( final String otherField ) {
    this.otherField = otherField;
  }

  /**
   * Returns the value of the other field. If the value is no comparable, this method returns <code>null</code> instead.
   *
   * @return the value of the other field.
   */
  protected Comparable getComparable() {
    final Object o = getDataRow().get( getOtherField() );
    if ( o instanceof Comparable ) {
      return (Comparable) o;
    }
    return null;
  }
}
