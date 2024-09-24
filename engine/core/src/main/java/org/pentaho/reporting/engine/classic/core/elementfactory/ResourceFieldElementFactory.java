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
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceFieldType;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * A factory to define ResourceFieldElements. ResourceField translate their content using a ResourceBundle instance.
 *
 * @author Thomas Morgner
 */
public class ResourceFieldElementFactory extends TextFieldElementFactory {
  /**
   * The resource base from which to read the translations.
   */
  private String resourceBase;

  /**
   * Default Constructor.
   */
  public ResourceFieldElementFactory() {
  }

  /**
   * Returns the base name of the resource bundle used to translate the content later.
   *
   * @return the resource bundle name of the element.
   */
  public String getResourceBase() {
    return resourceBase;
  }

  /**
   * Defines the base name of the resource bundle used to translate the content later.
   *
   * @param resourceBase
   *          the resource bundle name of the element.
   */
  public void setResourceBase( final String resourceBase ) {
    this.resourceBase = resourceBase;
  }

  /**
   * Creates the resource field element based on the set properties.
   *
   * @return the generated element.
   * @see org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = super.createElement();
    element.setElementType( new ResourceFieldType() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER, getResourceBase() );
    return element;
  }

  /**
   * creates a ResourceElement. ResourceElements resolve their value using a <code>java.util.ResourceBundle</code>.
   *
   * @param name
   *          the name of the element (null allowed)
   * @param bounds
   *          the element's bounds
   * @param color
   *          the text color of the element
   * @param alignment
   *          the element's horizontal text alignment
   * @param valignment
   *          the element's vertical text alignment
   * @param font
   *          the elements font
   * @param nullValue
   *          the text used when the value of this element is null
   * @param field
   *          the field in the datamodel to retrieve values from
   * @param resourceBase
   *          the classname/basename of the assigned resource bundle
   * @return the created ResourceElement
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createResourceElement( final String name, final Rectangle2D bounds, final Color color,
      final ElementAlignment alignment, final ElementAlignment valignment, final FontDefinition font,
      final String nullValue, final String resourceBase, final String field ) {
    final ResourceFieldElementFactory factory = new ResourceFieldElementFactory();
    factory.setX( new Float( bounds.getX() ) );
    factory.setY( new Float( bounds.getY() ) );
    factory.setMinimumWidth( new Float( bounds.getWidth() ) );
    factory.setMinimumHeight( new Float( bounds.getHeight() ) );
    factory.setName( name );
    factory.setColor( color );
    factory.setHorizontalAlignment( alignment );
    factory.setVerticalAlignment( valignment );

    if ( font != null ) {
      factory.setFontName( font.getFontName() );
      factory.setFontSize( new Integer( font.getFontSize() ) );
      factory.setBold( ElementFactory.getBooleanValue( font.isBold() ) );
      factory.setItalic( ElementFactory.getBooleanValue( font.isItalic() ) );
      factory.setEncoding( font.getFontEncoding( null ) );
      factory.setUnderline( ElementFactory.getBooleanValue( font.isUnderline() ) );
      factory.setStrikethrough( ElementFactory.getBooleanValue( font.isStrikeThrough() ) );
      factory.setEmbedFont( ElementFactory.getBooleanValue( font.isEmbeddedFont() ) );
    }
    factory.setNullString( nullValue );
    factory.setResourceBase( resourceBase );
    factory.setFieldname( field );
    return factory.createElement();
  }

}
