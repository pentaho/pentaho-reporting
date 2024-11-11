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

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;

public class RectangleType extends AbstractElementType {
  public static final RectangleType INSTANCE = new RectangleType();

  public RectangleType() {
    super( "rectangle" );
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
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final float arcWidth = parseArcParam( element, AttributeNames.Core.ARC_WIDTH );
    final float arcHeight = parseArcParam( element, AttributeNames.Core.ARC_HEIGHT );

    if ( arcWidth <= 0 || arcHeight <= 0 ) {
      return new Rectangle2D.Float( 0, 0, 100, 100 );
    }
    return new RoundRectangle2D.Float( 0, 0, 100, 100, arcWidth, arcHeight );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return getValue( runtime, element );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
    element.getStyle().setStyleProperty( ElementStyleKeys.DRAW_SHAPE, Boolean.TRUE );
  }

  private float parseArcParam( final ReportElement element, final String attrName ) {
    final float arcWidth;
    final Object attributeArcWidth = element.getAttribute( AttributeNames.Core.NAMESPACE, attrName );
    if ( attributeArcWidth != null ) {
      if ( attributeArcWidth instanceof Number ) {
        final Number n = (Number) attributeArcWidth;
        arcWidth = n.floatValue();
      } else {
        arcWidth = ParserUtil.parseFloat( String.valueOf( attributeArcWidth ), 0 );
      }
    } else {
      arcWidth = 0;
    }
    return arcWidth;
  }
}
