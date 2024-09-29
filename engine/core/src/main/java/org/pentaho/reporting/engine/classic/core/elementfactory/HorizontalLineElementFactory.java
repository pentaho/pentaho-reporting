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


package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.awt.Color;
import java.awt.Stroke;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.HorizontalLineType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * The drawable field element factory can be used to create elements that display <code>Drawable</code> elements.
 * <p/>
 * A drawable field expects the named datasource to contain Drawable objects.
 * <p/>
 * Once the desired properties are set, the factory can be reused to create similiar elements.
 *
 * @author Thomas Morgner
 */
public class HorizontalLineElementFactory extends AbstractContentElementFactory {
  /**
   * DefaultConstructor.
   */
  public HorizontalLineElementFactory() {
  }

  /**
   * Creates a new drawable field element based on the defined properties.
   *
   * @return the generated elements
   * @throws IllegalStateException
   *           if the field name is not set.
   * @see ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new HorizontalLineType() );
    return element;
  }

  public static Element createHorizontalLine( final float y ) {
    final HorizontalLineElementFactory ef = new HorizontalLineElementFactory();
    ef.setX( new Float( 0 ) );
    ef.setMinimumWidth( new Float( -100 ) );
    ef.setY( new Float( y ) );
    ef.setMinimumHeight( new Float( 0 ) );
    ef.setShouldDraw( Boolean.TRUE );
    ef.setScale( Boolean.TRUE );
    return ef.createElement();
  }

  public static Element createHorizontalLine( final float y, final Color color, final Stroke stroke ) {
    final Element element = createHorizontalLine( y );
    element.getStyle().setStyleProperty( ElementStyleKeys.PAINT, color );
    element.getStyle().setStyleProperty( ElementStyleKeys.STROKE, stroke );
    return element;
  }

}
