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
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A item sum function that only sums up the current value, if the value read from the conditionField is the same as the
 * value from the conditionValue property.
 *
 * @author Thomas Morgner
 * @deprecated Filter the values by using a plain formula.
 */
public class ConditionalItemSumFunction extends ItemSumFunction {
  /**
   * The name of the data-row column from where to read the comparison value for the condition.
   */
  private String conditionField;
  /**
   * The static comparison value for the condition.
   */
  private Object conditionValue;

  /**
   * Default Constructor.
   */
  public ConditionalItemSumFunction() {
  }

  /**
   * Returns the name of the data-row column from where to read the comparison value for the condition.
   *
   * @return a field name.
   */
  public String getConditionField() {
    return conditionField;
  }

  /**
   * Defines the name of the data-row column from where to read the comparison value for the condition.
   *
   * @param conditionField
   *          a field name.
   */
  public void setConditionField( final String conditionField ) {
    this.conditionField = conditionField;
  }

  /**
   * Returns the static comparison value for the condition.
   *
   * @return the static value.
   */
  public Object getConditionValue() {
    return conditionValue;
  }

  /**
   * Defines the static comparison value for the condition.
   *
   * @param conditionValue
   *          the static value.
   */
  public void setConditionValue( final Object conditionValue ) {
    this.conditionValue = conditionValue;
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event
   *          Information about the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( getConditionField() == null ) {
      return;
    }

    final Object currentValue = getDataRow().get( getConditionField() );
    // ObjectUtils-equal does not crash if both values are 'null'.
    // You could use the ordinary equals as well, but thats more code to write
    if ( ObjectUtilities.equal( currentValue, getConditionValue() ) ) {
      // this does the addition of the values.
      super.itemsAdvanced( event );
    }
  }
}
