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

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This function changes the color of the named elements according to the current value of a numeric field. If the value
 * of the field is not numeric (or null), the positive color is set.
 *
 * @author Thomas Morgner
 * @deprecated The same thing can be achieved using a simple StyleExpression on the element's PAINT stylekey.
 */
public class NegativeNumberPaintChangeFunction extends AbstractElementFormatFunction {
  /**
   * The name of the data-row column from where to read the numeric value.
   */
  private String field;
  /**
   * The color that is used for positive values.
   */
  private Color positiveColor;
  /**
   * The color that is used for negative values.
   */
  private Color negativeColor;
  /**
   * The color that is used for zero values.
   */
  private Color zeroColor;

  /**
   * Default Constructor.
   */
  public NegativeNumberPaintChangeFunction() {
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      final Color color = computeColor();
      e.getStyle().setStyleProperty( ElementStyleKeys.PAINT, color );
      return true;
    }
    return false;
  }

  /**
   * Computes the color that is applied to the elements.
   *
   * @return the computed color.
   */
  protected Color computeColor() {
    final Object o = getDataRow().get( getField() );
    if ( o instanceof Number == false ) {
      return getPositiveColor();
    }
    final Number n = (Number) o;
    final double d = n.doubleValue();
    if ( d < 0 ) {
      return getNegativeColor();
    }
    if ( d > 0 ) {
      return getPositiveColor();
    }
    final Color zeroColor = getZeroColor();
    if ( zeroColor == null ) {
      return getPositiveColor();
    }
    return zeroColor;
  }

  /**
   * Returns the color that is used if the number read from the field is positive.
   *
   * @return the color for positive values.
   */
  public Color getPositiveColor() {
    return positiveColor;
  }

  /**
   * Defines the color that is used if the number read from the field is positive.
   *
   * @param positiveColor
   *          the color for positive values.
   */
  public void setPositiveColor( final Color positiveColor ) {
    this.positiveColor = positiveColor;
  }

  /**
   * Returns the color that is used if the number read from the field is negative.
   *
   * @return the color for negative values.
   */
  public Color getNegativeColor() {
    return negativeColor;
  }

  /**
   * Defines the color that is used if the number read from the field is negative.
   *
   * @param negativeColor
   *          the color for negative values.
   */
  public void setNegativeColor( final Color negativeColor ) {
    this.negativeColor = negativeColor;
  }

  /**
   * Returns the color that is used if the number read from the field is zero.
   *
   * @return the color for zero values.
   */
  public Color getZeroColor() {
    return zeroColor;
  }

  /**
   * Defines the color that is used if the number read from the field is zero.
   *
   * @param zeroColor
   *          the color for zero values.
   */
  public void setZeroColor( final Color zeroColor ) {
    this.zeroColor = zeroColor;
  }

  /**
   * Returns the name of the data-row column from where to read the numeric value.
   *
   * @return the field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the data-row column from where to read the numeric value.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }
}
