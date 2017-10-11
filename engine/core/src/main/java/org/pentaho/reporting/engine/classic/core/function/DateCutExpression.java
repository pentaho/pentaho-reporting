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

import java.util.Date;

/**
 * Prunes a date in a calendar-unware way. This method can be used to zero the milli-seconds or seconds and so on from a
 * date-object. For more complex operations, the
 * {@link org.pentaho.reporting.engine.classic.core.function.date .VariableDateExpression} should be used instead.
 * <p/>
 * This expression simply executes a integer division followed by a integer multiplication on the milliseconds since
 * 01-01-1970. For a factor of 1000, this sets the milliseconds to zero.
 *
 * @author Martin Schmid
 * @deprecated The VariableDateExpression is much better suited for this purpose.
 */
public class DateCutExpression extends AbstractExpression {
  /**
   * The name of the data-row field from where to read the date that should be modified.
   */
  private String field;
  /**
   * The factor by which the date should be pruned.
   */
  private long factor;

  /**
   * Default Constructor. The factor defaults to 1000.
   */
  public DateCutExpression() {
    factor = 1000;
  }

  /**
   * Returns the name of the data-row field from where to read the date that should be modified.
   *
   * @return a field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the data-row field from where to read the date that should be modified.
   *
   * @param field
   *          a field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns the factor by which the date should be pruned.
   *
   * @return a factor.
   */
  public long getFactor() {
    return factor;
  }

  /**
   * Defines the factor by which the date should be pruned.
   *
   * @param factor
   *          a factor.
   */
  public void setFactor( final long factor ) {
    this.factor = factor;
  }

  /**
   * Computes the pruned date.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object date = getDataRow().get( getField() );
    if ( date instanceof Date == false ) {
      return null;
    }
    if ( factor == 0 || factor == 1 ) {
      return date;
    }
    final Date d = (Date) date;
    return new Date( ( d.getTime() / factor ) * factor );
  }
}
