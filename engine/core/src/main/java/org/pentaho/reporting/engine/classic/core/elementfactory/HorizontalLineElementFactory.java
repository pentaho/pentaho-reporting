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
