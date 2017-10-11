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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

/**
 * A report function that calculates the minimum value of one field (column) from the data-row. The function can be used
 * in two ways:
 * <ul>
 * <li>to calculate a minimum value for the entire report;</li>
 * <li>to calculate a minimum value within a particular group;</li>
 * </ul>
 * This function expects its input values to be either java.lang.Number instances or Strings that can be parsed to
 * java.lang.Number instances using a java.text.DecimalFormat.
 * <p/>
 * The function undestands two parameters, the <code>field</code> parameter is required and denotes the name of an
 * ItemBand-field which gets summed up.
 * <p/>
 * The parameter <code>group</code> denotes the name of a group. When this group is started, the counter gets reseted to
 * null.
 *
 * @author Thomas Morgner
 */
public class ItemMinFunction extends AbstractFunction implements FieldAggregationFunction {
  private static final Log logger = LogFactory.getLog( ItemMinFunction.class );

  /**
   * The minimum value.
   */
  private Sequence<Comparable> min;
  private transient int lastGroupSequenceNumber;

  /**
   * The name of the group on which to reset the count. This can be set to null to compute the minimum for the whole
   * report.
   */
  private String group;
  /**
   * The name of the field from where to read the values.
   */
  private String field;
  private String crosstabFilterGroup;

  /**
   * Constructs an unnamed function. Make sure to set a Name or function initialisation will fail.
   */
  public ItemMinFunction() {
    min = new Sequence<Comparable>();
  }

  /**
   * Constructs a named function.
   * <P>
   * The field must be defined before using the function.
   *
   * @param name
   *          The function name.
   */
  public ItemMinFunction( final String name ) {
    this();
    setName( name );
  }

  /**
   * Receives notification that a new report is about to start.
   * <P>
   * Does nothing.
   *
   * @param event
   *          Information about the event.
   */
  public void reportInitialized( final ReportEvent event ) {
    clear();
  }

  protected void clear() {
    this.lastGroupSequenceNumber = 0;
    this.min.clear();
  }

  /**
   * Receives notification that a new group is about to start. If this is the group defined for the function, then the
   * minimum value is reset to zero.
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
   * If a group is defined, the minimum value is reset to zero at the start of every instance of this group.
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
   * Receives notification that a row of data is being processed. Reads the data from the field defined for this
   * function and calculates the minimum value.
   *
   * @param event
   *          Information about the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( field == null ) {
      return;
    }

    final Object fieldValue = event.getDataRow().get( getField() );
    if ( fieldValue instanceof Comparable == false ) {
      return;
    }
    try {
      final Comparable compare = (Comparable) fieldValue;

      final Comparable oldValue = min.get( lastGroupSequenceNumber );
      if ( oldValue == null || oldValue.compareTo( compare ) > 0 ) {
        min.set( lastGroupSequenceNumber, compare );
      }
    } catch ( Exception e ) {
      ItemMinFunction.logger.error( "ItemMinFunction.advanceItems(): problem adding number." );
    }
  }

  public void summaryRowSelection( final ReportEvent event ) {
    if ( FunctionUtilities.isDefinedGroup( getCrosstabFilterGroup(), event ) ) {
      final int groupIndex = event.getState().getCurrentGroupIndex();
      this.lastGroupSequenceNumber = (int) event.getState().getCrosstabColumnSequenceCounter( groupIndex );
    }
  }

  /**
   * Returns the function value, in this case the running total of a specific column in the report's data row.
   *
   * @return The function value.
   */
  public Object getValue() {
    return min.get( lastGroupSequenceNumber );
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final ItemMinFunction function = (ItemMinFunction) super.getInstance();
    function.min = min.clone();
    function.lastGroupSequenceNumber = 0;
    return function;
  }

  public Object clone() {
    try {
      final ItemMinFunction function = (ItemMinFunction) super.clone();
      function.min = min.clone();
      return function;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public String getCrosstabFilterGroup() {
    return crosstabFilterGroup;
  }

  public void setCrosstabFilterGroup( final String crosstabFilterGroup ) {
    this.crosstabFilterGroup = crosstabFilterGroup;
  }

}
