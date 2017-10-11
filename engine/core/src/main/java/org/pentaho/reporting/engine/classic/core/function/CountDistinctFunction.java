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

import java.util.HashSet;

/**
 * Counts the distinct occurrences of an certain value of an column. This functionality is similar to the SQL distinct()
 * function.
 *
 * @author Thomas Morgner
 */
public class CountDistinctFunction extends AbstractFunction implements FieldAggregationFunction {
  /**
   * The collected values for the current group.
   */
  private Sequence<HashSet<Object>> values;
  private transient int lastGroupSequenceNumber;

  /**
   * The name of the group on which to reset the count. This can be set to null to compute the count for the whole
   * report.
   */
  private String group;
  /**
   * The field for which the number of distinct values are counted.
   */
  private String field;
  private String crosstabFilterGroup;

  private boolean ignoreNullValues;

  /**
   * DefaultConstructor.
   */
  public CountDistinctFunction() {
    values = new Sequence<HashSet<Object>>();
  }

  public boolean isIgnoreNullValues() {
    return ignoreNullValues;
  }

  public void setIgnoreNullValues( final boolean ignoreNullValues ) {
    this.ignoreNullValues = ignoreNullValues;
  }

  /**
   * Returns the group name.
   *
   * @return The group name.
   */
  public String getGroup() {
    return group;
  }

  /**
   * Sets the group name.
   * <P>
   * If a group is defined, the running total is reset to zero at the start of every instance of this group.
   *
   * @param name
   *          the group name (null permitted).
   */
  public void setGroup( final String name ) {
    this.group = name;
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    clear();
  }

  private void clear() {
    values.clear();
    lastGroupSequenceNumber = 0;
  }

  /**
   * Receives notification that a group has started.
   *
   * @param event
   *          the event.
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
   * Receives notification that a row of data is being processed.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( getField() == null ) {
      return;
    }

    final Object o = event.getDataRow().get( getField() );
    HashSet<Object> valueSet = this.values.get( lastGroupSequenceNumber );
    if ( valueSet == null ) {
      valueSet = new HashSet<Object>();
      this.values.set( lastGroupSequenceNumber, valueSet );
    }
    if ( ignoreNullValues && o == null ) {
      return;
    }

    valueSet.add( o );
  }

  /**
   * Return the number of distint values for the given column.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final HashSet vals = values.get( lastGroupSequenceNumber );
    if ( vals != null ) {
      return vals.size();
    }
    return 0;
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final CountDistinctFunction expression = (CountDistinctFunction) super.getInstance();
    expression.values = values.clone();
    expression.lastGroupSequenceNumber = 0;
    return expression;
  }

  /**
   * Clones the expression. The expression should be reinitialized after the cloning.
   * <P>
   * Expressions maintain no state, cloning is done at the beginning of the report processing to disconnect the
   * expression from any other object space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final CountDistinctFunction o = (CountDistinctFunction) super.clone();
    o.values = values.clone();
    return o;
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }
}
