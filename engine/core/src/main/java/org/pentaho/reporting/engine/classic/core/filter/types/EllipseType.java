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

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import java.awt.geom.Ellipse2D;
import java.util.Locale;

public class EllipseType extends AbstractElementType {
  public static final EllipseType INSTANCE = new EllipseType();

  public EllipseType() {
    super( "ellipse" );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element for which the data is computed.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return new Ellipse2D.Float( 0, 0, 100, 100 );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return getValue( runtime, element );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
    element.getStyle().setStyleProperty( ElementStyleKeys.DRAW_SHAPE, Boolean.TRUE );
  }
}
