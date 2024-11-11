/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
