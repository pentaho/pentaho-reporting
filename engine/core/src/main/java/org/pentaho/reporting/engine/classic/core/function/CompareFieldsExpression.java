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
