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

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

import java.math.BigDecimal;

/**
 * A report function that counts items in a report. If the "group" property is set, the item count is reset to zero
 * whenever the group changes.
 *
 * @author Thomas Morgner
 */
public class ItemCountFunction extends AbstractFunction implements AggregationFunction {
  public static final BigDecimal ONE = new BigDecimal( 1 );
  public static final BigDecimal ZERO = new BigDecimal( 0 );
  /**
   * The item count.
   */
  private Sequence<BigDecimal> count;
  private transient int lastGroupSequenceNumber;

  /**
   * The name of the group on which to reset the count. This can be set to null to compute the count for the whole
   * report.
   */
  private String group;
  private String crosstabFilterGroup;

  /**
   * Constructs an unnamed function.
   * <P>
   * This constructor is intended for use by the SAX handler class only.
   */
  public ItemCountFunction() {
    count = new Sequence<BigDecimal>();
  }

  /**
   * Constructs an item count report function.
   *
   * @param name
   *          The name of the function.
   * @throws NullPointerException
   *           if the name is null
   */
  public ItemCountFunction( final String name ) {
    setName( name );
  }

  protected void clear() {
    this.lastGroupSequenceNumber = 0;
    this.count.clear();
  }

  /**
   * Receives notification that a new report is about to start. The item count is set to zero.
   *
   * @param event
   *          the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    clear();
  }

  /**
   * Returns the name of the group (possibly null) for this function. The item count is reset to zero at the start of
   * each instance of this group.
   *
   * @return the group name.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Setss the name of the group for this function. The item count is reset to zero at the start of each instance of
   * this group. If the name is null, all items in the report are counted.
   *
   * @param group
   *          The group name.
   */
  public void setGroup( final String group ) {
    this.group = group;
  }

  /**
   * Receives notification that a new group is about to start. Checks to see if the group that is starting is the same
   * as the group defined for this function...if so, the item count is reset to zero.
   *
   * @param event
   *          Information about the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getGroup(), event ) ) {
      clear();
    }

    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Received notification of a move to the next row of data. Increments the item count.
   *
   * @param event
   *          Information about the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    final BigDecimal oldValue = count.get( lastGroupSequenceNumber );
    if ( oldValue == null ) {
      count.set( lastGroupSequenceNumber, ONE );
    } else {
      count.set( lastGroupSequenceNumber, oldValue.add( ONE ) );
    }
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Returns the number of items counted (so far) by the function. This is either the number of items in the report, or
   * the group (if a group has been defined for the function).
   *
   * @return The item count.
   */
  public Object getValue() {
    final BigDecimal value = count.get( lastGroupSequenceNumber );
    if ( value == null ) {
      return ZERO;
    }
    return value;
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

  public ItemCountFunction getInstance() {
    final ItemCountFunction function = (ItemCountFunction) super.getInstance();
    function.count = count.clone();
    function.lastGroupSequenceNumber = 0;
    return function;
  }

  public Object clone() {
    try {
      final ItemCountFunction clone = (ItemCountFunction) super.clone();
      clone.count = count.clone();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
