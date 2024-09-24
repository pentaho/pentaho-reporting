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
