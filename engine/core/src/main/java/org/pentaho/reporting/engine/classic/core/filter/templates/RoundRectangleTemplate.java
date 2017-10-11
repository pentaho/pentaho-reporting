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

package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.awt.geom.RoundRectangle2D;

/**
 * A template to create rectangle elements. The rectangle always has the width and the height of 100 points.
 * <p/>
 * This implementation is used to cover the common use of the rectangle shape element. Use the scaling feature of the
 * shape element to adjust the size of the rectangle.
 *
 * @author Thomas Morgner
 */
public class RoundRectangleTemplate extends AbstractTemplate {
  /**
   * The width of the arc that is used to round off the corners of rectangle.
   */
  private float arcWidth;
  /**
   * The height of the arc that is used to round off the corners of rectangle.
   */
  private float arcHeight;

  /**
   * Default Constructor.
   */
  public RoundRectangleTemplate() {
  }

  /**
   * Returns the width of the arc that is used to round off the corners of rectangle.
   *
   * @return the width of the corner arcs.
   */
  public float getArcWidth() {
    return arcWidth;
  }

  /**
   * Defines the width of the arc that is used to round off the corners of rectangle.
   *
   * @param arcWidth
   *          the width of the corner arcs.
   */
  public void setArcWidth( final float arcWidth ) {
    this.arcWidth = arcWidth;
  }

  /**
   * Returns the height of the arc that is used to round off the corners of rectangle.
   *
   * @return the height of the corner arcs.
   */
  public float getArcHeight() {
    return arcHeight;
  }

  /**
   * Defines the height of the arc that is used to round off the corners of rectangle.
   *
   * @param arcHeight
   *          the height of the corner arcs.
   */
  public void setArcHeight( final float arcHeight ) {
    this.arcHeight = arcHeight;
  }

  /**
   * Returns the template value, a RoundRectangle2D.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return a rectangle with a width and height of 100.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return new RoundRectangle2D.Float( 0, 0, 100, 100, arcWidth, arcHeight );
  }
}
