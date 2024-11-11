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

import java.math.BigDecimal;

/**
 * A report function that calculates the quotient of two summed fields (columns) from the data-row. This function
 * produces a global total. The total sum of the group is known when the group processing starts and the report is not
 * performing a prepare-run. The sum is calculated in the prepare run and recalled in the printing run.
 * <p/>
 * The function can be used in two ways:
 * <ul>
 * <li>to calculate a quotient for the entire report;</li>
 * <li>to calculate a quotient within a particular group;</li>
 * </ul>
 * This function expects its input values to be either java.lang.Number instances or Strings that can be parsed to
 * java.lang.Number instances using a java.text.DecimalFormat.
 * <p/>
 * The function undestands three parameters. The <code>dividend</code> parameter is required and denotes the name of an
 * ItemBand-field which gets summed up as dividend. The <code>divisor</code> parameter is required and denotes the name
 * of an ItemBand-field which gets summed up as divisor.
 * <p/>
 * The parameter <code>group</code> denotes the name of a group. When this group is started, the counter gets reseted to
 * null. This parameter is optional.
 * <p/>
 * This function scales the computed percentage to 100. A value of 100% will therefore be returned as 100 instead of 1.
 * The result of this function cannot be used together with the percentage operator of the NumberFormat in a
 * Number-field.
 *
 * @author Thomas Morgner
 */
public class TotalGroupSumQuotientPercentFunction extends TotalGroupSumQuotientFunction {
  /**
   * An internal constant.
   */
  private static final BigDecimal ONE_HUNDRED = new BigDecimal( 100 );

  /**
   * Default Constructor.
   */
  public TotalGroupSumQuotientPercentFunction() {
  }

  /**
   * Computes the scaled percentage.
   *
   * @return the computed percentage scaled to 100.
   */
  public Object getValue() {
    final Number value = (Number) super.getValue();
    if ( value == null ) {
      return null;
    }
    if ( value instanceof BigDecimal ) {
      return TotalGroupSumQuotientPercentFunction.ONE_HUNDRED.multiply( (BigDecimal) value );
    }

    return TotalGroupSumQuotientPercentFunction.ONE_HUNDRED.multiply( new BigDecimal( String.valueOf( value ) ) );
  }
}
