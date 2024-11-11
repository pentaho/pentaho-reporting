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


package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import java.awt.geom.Line2D;
import java.util.Locale;

public class HorizontalLineType extends AbstractElementType {
  public static final HorizontalLineType INSTANCE = new HorizontalLineType();

  public HorizontalLineType() {
    super( "horizontal-line" );
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
    return new Line2D.Float( 0, 0, 100, 0 );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return getValue( runtime, element );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
    element.getStyle().setStyleProperty( ElementStyleKeys.DRAW_SHAPE, Boolean.TRUE );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 0f ) );
  }
}
