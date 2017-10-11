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

import java.awt.geom.Line2D;

/**
 * Defines a horizontal line template. The line always has the width of 100 points. This implementation is used to cover
 * the common use of the line shape element. Use the scaling feature of the shape element to adjust the size of the
 * line.
 *
 * @author Thomas Morgner
 */
public class VerticalLineTemplate extends AbstractTemplate {
  /**
   * Default Constructor.
   */
  public VerticalLineTemplate() {
  }

  /**
   * Returns the template value, a vertical line.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return a vertical line with a height of 100.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return new Line2D.Float( 0, 0, 0, 100 );
  }
}
