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

package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.RectangleType;

/**
 * The drawable field element factory can be used to create elements that display <code>Drawable</code> elements.
 * <p/>
 * A drawable field expects the named datasource to contain Drawable objects.
 * <p/>
 * Once the desired properties are set, the factory can be reused to create similiar elements.
 *
 * @author Thomas Morgner
 */
public class RectangleElementFactory extends AbstractContentElementFactory {
  private Float arcWidth;
  private Float arcHeight;

  /**
   * DefaultConstructor.
   */
  public RectangleElementFactory() {
  }

  public Float getArcWidth() {
    return arcWidth;
  }

  public void setArcWidth( final Float arcWidth ) {
    this.arcWidth = arcWidth;
  }

  public Float getArcHeight() {
    return arcHeight;
  }

  public void setArcHeight( final Float arcHeight ) {
    this.arcHeight = arcHeight;
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

    element.setElementType( new RectangleType() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ARC_WIDTH, arcWidth );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ARC_HEIGHT, arcHeight );
    return element;
  }

  /**
   * A convenience method to create a filled rectangle element that scales its content-rectangle. Please note that
   * beginning with PRD-3.5, you can and should create backgrounds for elements via the "background-color" stylekey on
   * the band that contains the element.
   * <p/>
   * X, y, width and height can be positive numbers to give a absolute value or negative numbers for percentage values.
   *
   * @param x
   *          the x position of the element.
   * @param y
   *          the y position of the element.
   * @param width
   *          the width of the element.
   * @param height
   *          the height of the element.
   * @param color
   *          the fill color of the rectangle.
   * @return the created element.
   */
  public static Element createFilledRectangle( final float x, final float y, final float width, final float height,
      final Color color ) {
    final RectangleElementFactory rectangleElementFactory = new RectangleElementFactory();
    rectangleElementFactory.setX( new Float( x ) );
    rectangleElementFactory.setY( new Float( y ) );
    rectangleElementFactory.setMinimumWidth( new Float( width ) );
    rectangleElementFactory.setMinimumHeight( new Float( height ) );
    rectangleElementFactory.setColor( color );
    rectangleElementFactory.setShouldDraw( Boolean.FALSE );
    rectangleElementFactory.setShouldFill( Boolean.TRUE );
    rectangleElementFactory.setScale( Boolean.TRUE );
    return rectangleElementFactory.createElement();
  }
}
