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
 * A report function that calculates the quotient of two fields (columns) from the current row.
 * <p/>
 * This function expects its input values to be java.lang.Number instances.
 * <p/>
 * The function undestands two parameters. The <code>dividend</code> parameter is required and denotes the name of an
 * ItemBand-field which is used as dividend. The <code>divisor</code> parameter is required and denotes the name of an
 * ItemBand-field which is uses as divisor.
 * <p/>
 *
 * @author Heiko Evermann
 * @deprecated Use PercentageExpression instead, it's name is much clearer
 */
@SuppressWarnings( "deprecation" )
public class ItemColumnQuotientExpression extends PercentageExpression {
  /**
   * Constructs a new function.
   * <P>
   * Initially the function has no name...be sure to assign one before using the function.
   */
  public ItemColumnQuotientExpression() {
  }
}
