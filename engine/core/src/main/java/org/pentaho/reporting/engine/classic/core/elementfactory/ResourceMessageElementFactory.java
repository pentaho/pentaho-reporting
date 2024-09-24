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
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceMessageType;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * A factory to define translateable LabelElements. LabelElements are considered immutable and should not be modified
 * once they are created. The label expects plain text. The content of the label will be translated using an assigned
 * resource bundle.
 *
 * @author Thomas Morgner
 */
public class ResourceMessageElementFactory extends TextElementFactory {
  /**
   * The resource base from which to read the translations.
   */
  private String resourceBase;

  /**
   * The nullstring of the text element if the translation was not found.
   */
  private String nullString;
  /**
   * The resource key which is used to retrieve the translation.
   */
  private String formatKey;
  private String messageNullString;

  /**
   * DefaultConstructor.
   */
  public ResourceMessageElementFactory() {
  }

  public String getMessageNullString() {
    return messageNullString;
  }

  public void setMessageNullString( final String messageNullString ) {
    this.messageNullString = messageNullString;
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
   * Returns the null string for the text element. The null string is used when no content is found for that element.
   *
   * @return the null string.
   */
  public String getNullString() {
    return nullString;
  }

  /**
   * Defines the null string for the text element. The null string is used when no content is found for that element.
   * The nullstring itself can be null.
   *
   * @param nullString
   *          the null string.
   */
  public void setNullString( final String nullString ) {
    this.nullString = nullString;
  }

  /**
   * Returns the resource key that contains the label text.
   *
   * @return the label resource bundle key.
   */
  public String getFormatKey() {
    return formatKey;
  }

  /**
   * Defines the resource key, which will be used to read the translated label text.
   *
   * @param formatKey
   *          the resource bundle key.
   */
  public void setFormatKey( final String formatKey ) {
    this.formatKey = formatKey;
  }

  /**
   * Generates the element based on the defined properties.
   *
   * @return the generated element.
   * @throws NullPointerException
   *           if the resource class name is null.
   * @throws IllegalStateException
   *           if the resource key is not defined.
   * @see ElementFactory#createElement()
   */
  public Element createElement() {
    if ( getFormatKey() == null ) {
      throw new IllegalStateException( "ResourceKey is not set." );
    }

    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );
    element.setElementType( new ResourceMessageType() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER, getResourceBase() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, getFormatKey() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, getNullString() );
    element
        .setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.MESSAGE_NULL_VALUE, getMessageNullString() );
    return element;
  }

  /**
   * Creates a ResourceElement. ResourceElements resolve their value using a <code>java.util.ResourceBundle</code>.
   *
   * @param name
   *          the name of the new element.
   * @param bounds
   *          the bounds of the new element.
   * @param paint
   *          the text color of this text element.
   * @param alignment
   *          the horizontal alignment.
   * @param valign
   *          the vertical alignment.
   * @param font
   *          the font for this element.
   * @param resourceKey
   *          the key which is used to query the resource bundle
   * @param resourceBase
   *          the classname/basename of the assigned resource bundle
   * @param nullValue
   *          the null string of the text element (can be null).
   * @return the created ResourceElement
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createResourceMessage( final String name, final Rectangle2D bounds, final Color paint,
      final ElementAlignment alignment, final ElementAlignment valign, final FontDefinition font,
      final String nullValue, final String resourceBase, final String resourceKey ) {
    final ResourceMessageElementFactory factory = new ResourceMessageElementFactory();
    factory.setX( new Float( bounds.getX() ) );
    factory.setY( new Float( bounds.getY() ) );
    factory.setMinimumWidth( new Float( bounds.getWidth() ) );
    factory.setMinimumHeight( new Float( bounds.getHeight() ) );
    factory.setName( name );
    factory.setColor( paint );
    factory.setHorizontalAlignment( alignment );
    factory.setVerticalAlignment( valign );

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
    factory.setFormatKey( resourceKey );
    return factory.createElement();
  }

}
