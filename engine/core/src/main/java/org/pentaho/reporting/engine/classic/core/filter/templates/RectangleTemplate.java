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


package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.awt.geom.Rectangle2D;

/**
 * A template to create rectangle elements. The rectangle always has the width and the height of 100 points.
 * <p/>
 * This implementation is used to cover the common use of the rectangle shape element. Use the scaling feature of the
 * shape element to adjust the size of the rectangle.
 *
 * @author Thomas Morgner
 */
public class RectangleTemplate extends AbstractTemplate {
  /**
   * Default Constructor.
   */
  public RectangleTemplate() {
  }

  /**
   * Returns the template value, a Rectangle2D.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return a rectangle with a width and height of 100.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return new Rectangle2D.Float( 0, 0, 100, 100 );
  }
}
