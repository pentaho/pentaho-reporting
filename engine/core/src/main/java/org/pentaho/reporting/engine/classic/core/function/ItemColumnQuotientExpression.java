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
