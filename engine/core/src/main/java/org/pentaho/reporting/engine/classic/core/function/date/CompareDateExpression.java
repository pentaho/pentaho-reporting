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

package org.pentaho.reporting.engine.classic.core.function.date;

import org.pentaho.reporting.engine.classic.core.function.AbstractCompareExpression;

import java.util.Date;

/**
 * This expression compares a static date with the value read from a field.
 *
 * @author Thomas Morgner
 * @deprecated This can be solved easier using the Inline-Expression language.
 */
@SuppressWarnings( "deprecation" )
public class CompareDateExpression extends AbstractCompareExpression {
  /**
   * The static date that is used in the comparison.
   */
  private Date date;

  /**
   * Default Constructor.
   */
  public CompareDateExpression() {
  }

  /**
   * Returns the static date that is used for the comparison.
   *
   * @return the date.
   */
  public Date getDate() {
    return date;
  }

  /**
   * Defines the static date for the comparison. If no date is defined, this expression will always evaluate to false.
   *
   * @param date
   *          the static date for the comparison.
   */
  public void setDate( final Date date ) {
    this.date = date;
  }

  /**
   * Returns the static comparable.
   *
   * @return the static comparable.
   */
  protected Comparable getComparable() {
    return date;
  }
}
